#ifndef MAINWINDOW_H
#define MAINWINDOW_H

//#include "Panel.h"
#include "LeftPanel.h"
#include "RightPanel.h"

#include <QMainWindow>

//class LeftPanel;

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();

private slots:
    void changeActive(Panel::Type type);
    void updToolBar();
    void openProcess();
    void tabProcess();
    void dialogProcess();
    void showAbout();

    void parseProcess();

private:
    QToolBar *toolBar;
    QMenuBar *menuBar;
    QAction *openAction;
    QAction *showFATAction;
    QAction *aboutKey;

    LeftPanel* leftPanel;
    RightPanel* rightPanel;

    Panel::Type curPanel;
};

#endif

