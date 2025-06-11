#ifndef RIGHTPANEL_H
#define RIGHTPANEL_H

#include <QWidget>
#include <QFile>
#include <QStandardItemModel>
#include <QTreeView>

//#include "PanelType.h"
#include "Panel.h"
#include "FATParser.h"

class RightPanel : public Panel {
    Q_OBJECT

public:
    explicit RightPanel(QWidget *parent = nullptr);
    void showFAT(const QString  &path);

//    void onItemDoubleClicked(const QModelIndex &index) override;

private:
    QTreeView *rightWidget;
    QStandardItemModel *fileModel;

    QFile *file;
    FATParser *parser;

    QVector<FATParser::DirView> curDir;

    void draw(); // draw curDir
    void fillFileModel(QStandardItem *tar, const FATParser::DirView &origin);

//    void parseEBPB();
//    quint64 readRaw(const QByteArray &source, int offset, int size);
//    quint8 readRaw8(const QByteArray &source, int offset);
//    quint16 readRaw16(const QByteArray &source, int offset);
//    quint32 readRaw32(const QByteArray &source, int offset);

};

#endif // RIGHTPANEL_H
