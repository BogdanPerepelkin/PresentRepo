# Template.
TARGET = fatmanager
TEMPLATE = app

# Configs.
CONFIG += c++latest
CONFIG += strict_c++
CONFIG += qt

# Qt modules.
QT += core widgets gui

INCLUDEPATH += .
INCLUDEPATH += include/

HEADERS += \
    include/mainwindow.h \
    include/FATParser.h \
    include/LeftPanel.h \
    include/MyAbstractFileModel.h \
    include/MyFATModel.h \
    include/MyFileSystemModel.h \
    include/Panel.h \
    include/PanelType.h \
    include/RightPanel.h \
    include/mainwindow.h

SOURCES += \
    src/mainwindow.cpp \
    main.cpp \
    src/FATParser.cpp \
    src/LeftPanel.cpp \
    src/MyAbstractFileModel.cpp \
    src/MyFATModel.cpp \
    src/MyFileSystemModel.cpp \
    src/Panel.cpp \
    src/RightPanel.cpp \
    src/mainwindow.cpp


# Add resources as needed.
# File 'resources.qrc' is expected in the repo's root.
#RESOURCES += \
#    resources.qrc
