#include "Panel.h"

Panel::Panel(Type type, QWidget *parent) :
    QWidget(parent),
    panelType(type)
{

}

Panel::Type Panel::getType() const {
    return panelType;
}

void Panel::onItemDoubleClicked(const QModelIndex &index) {
    Q_UNUSED(index);
}
