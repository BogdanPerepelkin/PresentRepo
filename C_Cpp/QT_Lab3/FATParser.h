#ifndef FATPARSER_H
#define FATPARSER_H

#include <QFile>
#include <QDateTime>
#include <QVector>


class FATParser {
public:
    explicit FATParser(QFile *file);

    struct DirView {
        QString name;
        bool isDir;
        quint32 size;
        QDateTime bornDate;
        quint32 firstCluster;
        QVector<DirView> children;
    };

    DirView parseFAT();
    QVector<DirView> parseDir(quint32 clusterIndex);
//    DirView parseDir(quint32 clusterIndex);

private:
    QFile *file;

    struct EBPB {
        quint16 sectorSize;
        quint8 numSectorsInCluster;
        quint16 reserveSize;
        quint8 numFATCopy;
//        quint16 rootEntryCount;
//        quint16 totalSectors16;
//        quint16 sectorsPerFAT16;
//        quint32 totalSectors32;
        quint32 numSectorsInFAT;
        quint32 rootIndex;
        quint16 fsinfoSector;
    } ebpb;

    struct FSInfo {
        quint32 numFreeCluster;
        quint32 nextFreeCluster;
        bool isValid;
    } fsinfo;

    quint64 fatOffset;
    quint64 dataOffset;
    quint64 rootDirOffset;

    DirView parseRootDir();

    void parseEBPB();
    void parseFSInfo();
    void calcOffsets();
    QString parseLFN(const QByteArray &);

    quint64 readRaw(const QByteArray &source, int offset, int size);
    quint8 readRaw8(const QByteArray &source, int offset);
    quint16 readRaw16(const QByteArray &source, int offset);
    quint32 readRaw32(const QByteArray &source, int offset);
};
Q_DECLARE_METATYPE(FATParser::DirView);

#endif // FATPARSER_H




















































