#include "FATParser.h"

#include <QMessageBox>
#include <QtDebug>

FATParser::FATParser(QFile *file) :
    file(file)
{}

FATParser::DirView FATParser::parseFAT() {
    parseEBPB();
    parseFSInfo();
    calcOffsets();
    return parseRootDir();
}

quint64 FATParser::readRaw(const QByteArray &source, int offset, int size) {
    quint64 result = 0;
    for (int i = 0; i < size; ++i) {
        result |= static_cast<quint64>(static_cast<quint8>(source[offset + i])) << (8 * i);
    }
    return result;
}

quint8 FATParser::readRaw8(const QByteArray &source, int offset) {
    return static_cast<quint8>(readRaw(source, offset, 1));
}

quint16 FATParser::readRaw16(const QByteArray &source, int offset) {
    return static_cast<quint16>(readRaw(source, offset, 2));
}

quint32 FATParser::readRaw32(const QByteArray &source, int offset) {
    return static_cast<quint32>(readRaw(source, offset, 4));
}

void FATParser::parseEBPB() {
    QByteArray source = file->read(512);
    if (source.size() != 512) {
        QMessageBox::warning(nullptr, "Error", "Failed to read boot sector.");
        return;
    }

    ebpb.sectorSize = readRaw16(source, 11);
    ebpb.numSectorsInCluster = readRaw8(source, 13);
    ebpb.reserveSize = readRaw16(source, 14);
    ebpb.numFATCopy = readRaw8(source, 16);
//    quint16 rootEntryCount = readLE16(17);
//    quint16 totalSectors16 = readLE16(19);
//    quint16 sectorsPerFAT16 = readLE16(22);
//    quint32 totalSectors32 = readLE32(32);
    ebpb.numSectorsInFAT = readRaw32(source, 36);
    // rootCluster
    ebpb.rootIndex = readRaw32(source, 44); // FAT32 only
    ebpb.fsinfoSector = readRaw16(source, 48);
}

void FATParser::parseFSInfo() {
    int fsinfoOffset = ebpb.fsinfoSector * ebpb.sectorSize;
    if (!file->seek(fsinfoOffset)) {
        qWarning("Error read fsinfo");
        return;
    }
    QByteArray source = file->read(512);
    if (source.size() != 512) {
        qWarning("Error read fsinfo");
        return;
    }

    quint32 openSignature = readRaw32(source, 0x00);
    quint32 closeSignature = readRaw32(source, 0x1FC);
    if (!(openSignature == 0x41615252 && closeSignature == 0xAA550000)) {
        qWarning("Error dopisat");
        return;
    }

    fsinfo.numFreeCluster = readRaw32(source, 0x1E4);
    fsinfo.nextFreeCluster = readRaw32(source, 0x1E8);
}

void FATParser::calcOffsets() {
    fatOffset = ebpb.reserveSize * ebpb.sectorSize;
    dataOffset = fatOffset + (ebpb.numFATCopy * ebpb.numSectorsInFAT * ebpb.sectorSize);
    rootDirOffset = dataOffset + ((ebpb.rootIndex - 2) * ebpb.numSectorsInCluster * ebpb.sectorSize);
}

QString FATParser::parseLFN(const QByteArray &source) {
    QString result;

    auto parsePart = [&](int offset, int length) {
        for (int i = 0; i < length; i += 2) {
            quint16 ch = readRaw16(source, offset + i);
            if (ch == 0x0000 || ch == 0xFFFF) break;
            result.append(QChar(ch)); // UTF-16
        }
    };

    parsePart(1, 10); // 1–10
    parsePart(14, 12); // 14–25
    parsePart(28, 4); // 28–31

    return result;
}

QVector<FATParser::DirView> FATParser::parseDir(quint32 clusterIndex) {
    qDebug() << "i am inside parseDir";
    quint32 clusterSize = ebpb.sectorSize * ebpb.numSectorsInCluster;
    quint32 offset = dataOffset + (clusterIndex - 2) * clusterSize;

    if (!file) {
        qDebug() << "error @@@";
        qWarning("file is nullptr");
        return {};
    }

    qDebug() << file;

    // debug
    if (offset + clusterSize > file->size()) {
        qDebug() << "Cluster out of bounds";
        qWarning("Cluster out of bounds");
        return {};
    }

    if (!file->seek(offset)) {
        qDebug() << "error @";
        qWarning("Error @@");
        return {};
    }

    QByteArray source = file->read(clusterSize);
    if (source.size() != static_cast<int>(clusterSize)) {
        qDebug() << "error @@";
        qWarning("Error @");
        return {};
    }

    QVector<DirView> rootDir;
    QStringList accumLFN;
    for (int offset = 0; offset + 32 <= source.size(); offset += 32) {
        quint8 firstByte = readRaw8(source, offset);

        if (firstByte == 0x00) break;
        if (firstByte == 0xE5) continue;

        quint8 attr = readRaw8(source, offset + 11);
        if (attr == 0x0F) {
            accumLFN.prepend(parseLFN(source.mid(offset, 32)));
            continue;
        }

        QString name = QString::fromLatin1(source.mid(offset, 8)).trimmed();
        QString ext = QString::fromLatin1(source.mid(offset + 8, 3)).trimmed();
        QString fullName = ext.isEmpty() ? name : name + "." + ext;

        quint16 firstClusterHi = readRaw16(source, offset + 20);
        quint16 firstClusterLo = readRaw16(source, offset + 26);
        quint32 firstCluster = (firstClusterHi << 16) | firstClusterLo;

        quint32 fileSize = readRaw32(source, offset + 28);

        DirView curSubj;
        curSubj.name = accumLFN.isEmpty() ? fullName : accumLFN.join("");
        curSubj.isDir = attr & 0x10;
        curSubj.size = fileSize;
        curSubj.bornDate = QDateTime();

        curSubj.firstCluster = firstCluster;

        qDebug() << "Checking:" << curSubj.name << "cluster:" << firstCluster;

        if (curSubj.isDir) {
            if (firstCluster < 2) {
                curSubj.children = {};
            } else if (curSubj.name != "." && curSubj.name != "..") {
                curSubj.children = parseDir(firstCluster);
            }
//            curSubj.children = parseDir(firstCluster);
        }

        rootDir.append(curSubj);
        accumLFN.clear();
    }
    return rootDir;
}

FATParser::DirView FATParser::parseRootDir() {
    DirView plug;
    plug.name = QString();
    plug.isDir = false;
    plug.size = 0;
    plug.bornDate = QDateTime();
    plug.firstCluster = 0;
    plug.children = parseDir(ebpb.rootIndex);

    return plug;
//    return parseDir(ebpb.rootIndex);
}





















