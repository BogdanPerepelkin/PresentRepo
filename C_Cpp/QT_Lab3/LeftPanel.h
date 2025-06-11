#ifndef LEFTPANEL_H
#define LEFTPANEL_H

#include "MyFileSystemModel.h"
#include "Panel.h"

#include <QWidget>
#include <QTreeView>
#include <QPushButton>

class LeftPanel : public Panel {
    Q_OBJECT

public:
    explicit LeftPanel(QWidget* parent = nullptr);
    bool eventFilter(QObject* watched, QEvent* event) override;
    void go2Dir(const QModelIndex &index);
    QModelIndex getCurIndex() const;
    void setMyFocus();
    void highlight();
    void setPathlineFocus();
    QString getFilepath() const;

//    void onItemDoubleClicked(const QModelIndex &index) override;

signals:
//    void focusedOn(PanelType type);
    void changedPath(const QModelIndex &index);

private slots:
    void updPath(const QModelIndex &index);
    void clickFallinButton();
    void clickBackButton();
    void clickForButton();
    void enterPath();

private:
    MyFileSystemModel *fileModel;
    QTreeView *leftWidget;
    QPushButton *fallinButton;
    QPushButton *backButton;
    QPushButton *forButton;
    QLineEdit *pathLine;
    QStack<QModelIndex> *memStack;
    QStack<QModelIndex> *frontStack;
//    PanelType panelType;
};

#endif // LEFTPANEL_H
