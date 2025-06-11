#ifndef PANEL_H
#define PANEL_H

#include <QWidget>

class Panel : public QWidget {
    Q_OBJECT

public:
    enum class Type {
        Left,
        Right
    };
    explicit Panel(Type type, QWidget* parent = nullptr);
    Type getType() const;

    virtual void onItemDoubleClicked(const QModelIndex &index);

signals:
    void focusedOn(Panel::Type type);

private:
    Type panelType;
};

#endif // PANEL_H
