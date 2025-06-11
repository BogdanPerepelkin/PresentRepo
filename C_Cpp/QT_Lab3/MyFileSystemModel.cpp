#include "MyFileSystemModel.h"

#include <QLocale>
#include <QFileInfo>
#include <QDateTime>

MyFileSystemModel::MyFileSystemModel(QObject* parent) :
    QFileSystemModel(parent)
{
    this->setRootPath(QDir::rootPath());
    this->setReadOnly(true);
    this->setResolveSymlinks(true);
}

QVariant MyFileSystemModel::data(const QModelIndex &index, int role) const
{
    if (role == Qt::DisplayRole) {
        QFileInfo info = this->fileInfo(index);
        if (index.column() == 1) {
            if (info.isDir()) {
                return "Dir";
            } else {
                qint64 size = info.size();
                return QLocale().toString(size);
            }
        } else if (index.column() == 2) {
            QDateTime birth = info.birthTime();
            if (!birth.isValid()) {
                return "unavailable";
            }
            return birth.toString("HH:mm:ss");
        }
    }

    return QFileSystemModel::data(index, role);
}

QVariant MyFileSystemModel::headerData(int section, Qt::Orientation orientation, int role) const
{
    if (role == Qt::DisplayRole) {
        switch (section) {
            case 0: return "Name";
            case 1: return "Size";
            case 2: return "Time";
            default: return QFileSystemModel::headerData(section, orientation, role);
        }
    }
    return QFileSystemModel::headerData(section, orientation, role);
}
