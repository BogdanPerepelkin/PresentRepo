#include "LeftPanel.h"
#include "MyFileSystemModel.h"

#include <QMessageBox>

#include <QVBoxLayout>
#include <QTreeView>
#include <QLineEdit>
#include <QStack>
#include <QMouseEvent>
#include <QShortcut>
#include <QApplication>

#include <QDebug>

LeftPanel::LeftPanel(QWidget* parent) :
//    QWidget(parent),
    Panel(Panel::Type::Left, parent),
    fileModel(new MyFileSystemModel(this)),
    leftWidget(new QTreeView(this)),
    fallinButton(new QPushButton("fall in (2)", this)),
    backButton(new QPushButton("back (3)", this)),
    forButton(new QPushButton("forward (4)", this)),
    pathLine(new QLineEdit(this)),
    memStack(new QStack<QModelIndex>()),
    frontStack(new QStack<QModelIndex>())
//    panelType(type)
{
    leftWidget->setModel(fileModel);
//    leftWidget->setRootIndex(fileModel->index(QDir::currentPath()));

    leftWidget->setRootIndex(fileModel->index("C:/my_folders/CT_dance/CPlusPlus/QT_lab3"));

    leftWidget->setColumnWidth(0, 359);
    leftWidget->setColumnWidth(1, 100);
    leftWidget->setColumnHidden(3, true);
    leftWidget->setColumnHidden(4, true);

    leftWidget->setSortingEnabled(true);
    leftWidget->sortByColumn(0, Qt::AscendingOrder);

    leftWidget->setSelectionMode(QAbstractItemView::ExtendedSelection);
    leftWidget->setEditTriggers(QAbstractItemView::NoEditTriggers);

    pathLine->setText(QDir::currentPath());

    // signals:
    connect(leftWidget, &QTreeView::doubleClicked, this, &LeftPanel::go2Dir);
    connect(fallinButton, &QPushButton::clicked, this, &LeftPanel::clickFallinButton);
    connect(backButton, &QPushButton::clicked, this, &LeftPanel::clickBackButton);
    connect(forButton, &QPushButton::clicked, this, &LeftPanel::clickForButton);
    connect(pathLine, &QLineEdit::returnPressed, this, &LeftPanel::enterPath);
    connect(this, &LeftPanel::changedPath, this, &LeftPanel::updPath);

    // shortcuts:
    QShortcut *sc_fallin = new QShortcut(Qt::Key_2, this);
    sc_fallin->setContext(Qt::WidgetWithChildrenShortcut); // magic for focus settings
    connect(sc_fallin, &QShortcut::activated, this, &LeftPanel::clickFallinButton);

    QShortcut *sc_back = new QShortcut(Qt::Key_3, this);
    sc_back->setContext(Qt::WidgetWithChildrenShortcut); // magic for focus settings
    connect(sc_back, &QShortcut::activated, this, &LeftPanel::clickBackButton);

    QShortcut *sc_forward = new QShortcut(Qt::Key_4, this);
    sc_forward->setContext(Qt::WidgetWithChildrenShortcut);
    connect(sc_forward, &QShortcut::activated, this, &LeftPanel::clickForButton);

    // interface:
    QHBoxLayout* pathLayout = new QHBoxLayout;
    pathLayout->addWidget(fallinButton);
    pathLayout->addWidget(backButton);
    pathLayout->addWidget(forButton);
    pathLayout->addWidget(pathLine);

    QVBoxLayout* mainLayout = new QVBoxLayout(this);
    mainLayout->addLayout(pathLayout);
    mainLayout->addWidget(leftWidget);
    setLayout(mainLayout);

    leftWidget->viewport()->installEventFilter(this);
    backButton->installEventFilter(this);
    forButton->installEventFilter(this);
}

bool LeftPanel::eventFilter(QObject *obj, QEvent *event) {
    if (event->type() == QEvent::MouseButtonPress) {
        this->setFocus();
        emit focusedOn(getType());
        return false;
    }
    return QWidget::eventFilter(obj, event);
}

void LeftPanel::go2Dir(const QModelIndex &index) {
    if (index.column() != 0) return;

    if (fileModel->isDir(index)) {
        memStack->push(leftWidget->rootIndex());
        leftWidget->setRootIndex(index);
        emit changedPath(index);
    }
}

QModelIndex LeftPanel::getCurIndex() const {
    return leftWidget->currentIndex();
}

void LeftPanel::setMyFocus() {
    leftWidget->setFocus();
}

void LeftPanel::highlight() {
    QModelIndex curIndex = getCurIndex();
//    if (leftWidget->selectionModel()->selectedIndexes().isEmpty()) {
//        curIndex = fileModel->index(0, 0, leftWidget->rootIndex());
//        leftWidget->setCurrentIndex(curIndex);
//    }
//    if (!curIndex.isValid()) {
//        QMessageBox::warning(this, "Error", "higlight nothing.");
//          return;
//    }
    leftWidget->scrollTo(curIndex);
}

void LeftPanel::setPathlineFocus() {
    pathLine->selectAll();
    pathLine->setFocus();
}

QString LeftPanel::getFilepath() const {
    return fileModel->filePath(getCurIndex());
}

void LeftPanel::updPath(const QModelIndex &index) {
    pathLine->setText(fileModel->filePath(index));
    leftWidget->setRootIndex(index);
}

void LeftPanel::clickFallinButton() {
    go2Dir(getCurIndex());
}

void LeftPanel::clickBackButton() {
    if (!memStack->isEmpty()) {
        frontStack->push(leftWidget->rootIndex());
        QModelIndex prev = memStack->pop();
        leftWidget->setRootIndex(prev);
        emit changedPath(prev);
    }
}

void LeftPanel::clickForButton() {
    if (!frontStack->isEmpty()) {
        memStack->push(leftWidget->rootIndex());
        QModelIndex last = frontStack->pop();
        leftWidget->setRootIndex(last);
        emit changedPath(last);
    }
}

void LeftPanel::enterPath() {
    QModelIndex index = fileModel->index(pathLine->text());
    this->setFocus();
    emit focusedOn(getType());
    if (index.isValid() && fileModel->isDir(index)) {
        go2Dir(index);
    } else {
        QMessageBox::warning(this, "Error", "Wrong path.");
        emit changedPath(leftWidget->rootIndex());
    }
}
