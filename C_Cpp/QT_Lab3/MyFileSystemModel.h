#ifndef MYFILESYSTEMMODEL_H
#define MYFILESYSTEMMODEL_H

#include <QFileSystemModel>

class MyFileSystemModel : public QFileSystemModel
{
    Q_OBJECT

public:
    explicit MyFileSystemModel(QObject* parent = nullptr);
    QVariant data(const QModelIndex &index, int role = Qt::DisplayRole) const override;
    QVariant headerData(int section, Qt::Orientation orientation, int role = Qt::DisplayRole) const override;
};

#endif // MYFILESYSTEMMODEL_H
