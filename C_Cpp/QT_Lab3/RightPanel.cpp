// XRicya

#include <QMessageBox>
#include <QVBoxLayout>
#include <QDebug>

#include "RightPanel.h"


RightPanel::RightPanel(QWidget *parent) :
    Panel(Panel::Type::Right, parent),
    rightWidget(new QTreeView(this)),
    fileModel(new QStandardItemModel(this)),
    file(nullptr),
    parser(nullptr)
{
    // interface
    QVBoxLayout *mainLayout = new QVBoxLayout(this);
    mainLayout->addWidget(rightWidget);
    setLayout(mainLayout);

    connect(rightWidget, &QTreeView::doubleClicked, this, &RightPanel::onItemDoubleClicked);
}

void RightPanel::showFAT(const QString &path) {
//    QFile file(path);
    file = new QFile(path);
    if (!file->open(QIODevice::ReadOnly)) {
        qDebug() << path;
        QMessageBox::warning(this, "Error", "Open FAT.");
        return;
    }

    parser = new FATParser(file);
    FATParser::DirView rootDir = parser->parseFAT();

    fileModel = new QStandardItemModel(this);
    fileModel->setHorizontalHeaderLabels({ "Name", "Size", "Time" });

//    for (const auto &curObj : qAsConst(curDir)) {
//        QList<QStandardItem *> curRow;
//        curRow << new QStandardItem(curObj.name)
//              << new QStandardItem(curObj.isDir ? "Dir" : QString::number(curObj.size))
//              << new QStandardItem(curObj.bornDate.toString("yyyy-MM-dd HH:mm"));
//        fileModel->appendRow(curRow);
//    }

    rightWidget->setModel(fileModel);
    rightWidget->expandAll();
}

void RightPanel::fillFileModel(QStandardItem *parent, const FATParser::DirView &origin) {
    QList<QStandardItem *> row;
    QStandardItem *name = new QStandardItem(origin.name);
    name->setData(QVariant::fromValue(origin)); // ???

    row << name
        << new QStandardItem(origin.isDir ? "Dir" : QString::number(origin.size))
        << new QStandardItem(origin.bornDate.toString("yyyy-MM-dd HH:mm"));

    if (origin.name.isEmpty()) {
        name = nullptr;
        // pass
    } else if (parent == nullptr) {
        fileModel->appendRow(row);
    } else {
        parent->appendRow(row);
    }

    if (origin.isDir) {
        for (const FATParser::DirView &child : origin.children) {
            fillFileModel(name, child);
        }
    }
}

//void RightPanel::onItemDoubleClicked(const QModelIndex &index) {
//    qDebug() << "double ckick right!";
//    QString reqName = index.sibling(index.row(), 0).data().toString();

//    FATParser::DirView reqDir;
//    bool flagExist = false;
//    for (const FATParser::DirView &view : curDir) {
//        if (view.name == reqName && view.isDir) {
//            reqDir = view;
//            flagExist = true;
//            break;
//        }
//    }
//    if (!flagExist) return;

//    qDebug() << reqName;

//    QVector<FATParser::DirView> ownReqDir = parser->parseDir(reqDir.firstCluster);
//    curDir = ownReqDir;

//    qDebug() << "after parse";

//    fileModel = new QStandardItemModel(this);
//    fileModel->setHorizontalHeaderLabels({ "Name", "Size", "Time" });

//    for (const auto &curObj : ownReqDir) {
//        QList<QStandardItem *> row;
//        row << new QStandardItem(curObj.name)
//            << new QStandardItem(curObj.isDir ? "Dir" : QString::number(curObj.size))
//            << new QStandardItem(curObj.bornDate.toString("yyyy-MM-dd HH:mm"));
//        fileModel->appendRow(row);
//    }

//    rightWidget->setModel(fileModel);
//    rightWidget->expandAll();
//}

void RightPanel::draw() {
    fileModel = new QStandardItemModel(this);
    fileModel->setHorizontalHeaderLabels({ "Name", "Size", "Time" });

    for (const auto &curObj : curDir) {
        QList<QStandardItem *> row;
        row << new QStandardItem(curObj.name)
            << new QStandardItem(curObj.isDir ? "Dir" : QString::number(curObj.size))
            << new QStandardItem(curObj.bornDate.toString("yyyy-MM-dd HH:mm"));
        fileModel->appendRow(row);
    }

    rightWidget->setModel(fileModel);
    rightWidget->expandAll();
}
















