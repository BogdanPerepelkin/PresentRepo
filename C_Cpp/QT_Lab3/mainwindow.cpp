#include "mainwindow.h"
#include "LeftPanel.h"
#include "RightPanel.h"
//#include "PanelType.h"

#include <QWidget>
#include <QHBoxLayout>
#include <QToolBar>
#include <QMenuBar>
#include <QKeyEvent>
#include <QApplication>
#include <QShortcut>
#include <QMessageBox>
#include <QDebug>

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    toolBar(new QToolBar(this)),
    menuBar(new QMenuBar(this)),
//    openAction(new QAction("open (2)", this)),
    showFATAction(new QAction("show FAT (F2)", this)),
    aboutKey(new QAction("hot keys", this)),
    leftPanel(new LeftPanel(this)),
    rightPanel(new RightPanel(this))
{
    // signals:
    connect(leftPanel, &Panel::focusedOn, this, &MainWindow::changeActive);
    connect(rightPanel, &Panel::focusedOn, this, &MainWindow::changeActive);
//    connect(openAction, &QAction::triggered, this, [this]() {
//        leftPanel->go2Dir(leftPanel->getCurIndex());
//    });
    connect(showFATAction, &QAction::triggered, this, [this]() {
        qDebug() << leftPanel->getFilepath();
        rightPanel->showFAT(leftPanel->getFilepath());
    });
    connect(aboutKey, &QAction::triggered, this, &MainWindow::showAbout);

    // Shortcuts:
    QShortcut *sc_Tab = new QShortcut(Qt::Key_Tab, this);
//    QShortcut *sc_2 = new QShortcut(Qt::Key_2, this);
    QShortcut *sc_F4 = new QShortcut(Qt::Key_F4, this);
    QShortcut *sc_F10 = new QShortcut(Qt::Key_F10, this);
    QShortcut *sc_F1 = new QShortcut(Qt::Key_F1, this);
    QShortcut *sc_F2 = new QShortcut(Qt::Key_F2, this);
    connect(sc_Tab, &QShortcut::activated, this, &MainWindow::tabProcess);
//    connect(sc_2, &QShortcut::activated, this, &MainWindow::openProcess);
    connect(sc_F4, &QShortcut::activated, this, &MainWindow::dialogProcess);
    connect(sc_F10, &QShortcut::activated, this, &QWidget::close);
    connect(sc_F1, &QShortcut::activated, this, &MainWindow::showAbout);
    connect(sc_F2, &QShortcut::activated, this, &MainWindow::parseProcess);

    // toolbar:
    addToolBar(toolBar);
    changeActive(Panel::Type::Left);

    // menubar:
    QMenu *aboutPoint = menuBar->addMenu("About"); // menuBar -> aboutPoint
    QMenu *filePoint = menuBar->addMenu("File");
    aboutPoint->addAction(aboutKey);
    filePoint->addAction(nullptr);
    setMenuBar(menuBar);

    // focus:
    this->setFocusPolicy(Qt::StrongFocus);
    this->setFocus();

    // interface:
    QWidget *centralWidget = new QWidget(this);
    QHBoxLayout *layout = new QHBoxLayout(centralWidget);
    layout->addWidget(leftPanel);
    layout->addWidget(rightPanel);
    centralWidget->setLayout(layout);
    setCentralWidget(centralWidget);

    // window:
    setWindowTitle("DirOpus killer");
    resize(1440, 720);
}

MainWindow::~MainWindow() {}

void MainWindow::changeActive(Panel::Type type) {
    curPanel = type;
    leftPanel->setMyFocus();
    updToolBar();
}

void MainWindow::updToolBar() {
    toolBar->clear();
    if (curPanel == Panel::Type::Left) {
//        toolBar->addAction(openAction);
        // TODO f2
    } else {
        // TODO f4
    }
}

void MainWindow::openProcess() {
    if (curPanel == Panel::Type::Left) openAction->trigger();
}

void MainWindow::tabProcess() {
    curPanel = (curPanel == Panel::Type::Left) ? Panel::Type::Right : Panel::Type::Left;
    updToolBar();

    if (curPanel == Panel::Type::Left) {
        leftPanel->setMyFocus();
        leftPanel->highlight();
    } else {
        rightPanel->setFocus();
    }
}

void MainWindow::dialogProcess() {
    if (curPanel == Panel::Type::Left) {
        leftPanel->setPathlineFocus();
    }
}

void MainWindow::showAbout() {
    QMessageBox::about(this, "About", "Kill me maybe");
}

void MainWindow::parseProcess() {
    if (curPanel == Panel::Type::Left) showFATAction->trigger();
}
