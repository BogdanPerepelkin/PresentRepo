QT += core gui
greaterThan(QT_MAJOR_VERSION, 4): QT += widgets
CONFIG += c++11

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
    src/main.cpp \
    src/FATParser.cpp \
    src/LeftPanel.cpp \
    src/MyAbstractFileModel.cpp \
    src/MyFATModel.cpp \
    src/MyFileSystemModel.cpp \
    src/Panel.cpp \
    src/RightPanel.cpp \
    src/mainwindow.cpp

qnx: target.path = /tmp/$${TARGET}/bin
else: unix:!android: target.path = /opt/$${TARGET}/bin
!isEmpty(target.path): INSTALLS += target

# Add resources as needed.
# File 'resources.qrc' is expected in the repo's root.
#RESOURCES += \
#    resources.qrc

