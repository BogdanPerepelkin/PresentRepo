#include <string>

bool password(const std::string &password) {
    bool uppercase = false;
    bool lowercase = false;
    bool number = false;
    int length = 0;

    for (const char &symb : password) {
        length++;
        if (symb >= 'a' && symb <= 'z') {
            lowercase = true;
        } else if (symb >= 'A' && symb <= 'Z') {
            uppercase = true;
        } else if (symb >= '0' && symb <= '9') {
            number = true;
        }
        if (length >= 8 && uppercase && lowercase && number) {
            return true;
        }
    }

    return false;
}
