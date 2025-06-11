#include <sstream>

class X {
    int m_a;
    int m_b;
public:
    X() {
        this->m_a = 1;
        this->m_b = 2;
    }
    X(const int single) {
        this->m_a = single;
        this->m_b = 2;
    }
    X(const int cur_m_a, const int cur_m_b) {
        this->m_a = cur_m_a;
        this->m_b = cur_m_b;
    }

    X &operator+=(const X& right) {
        m_a += right.m_a;
        m_b += right.m_b;
        return *this;
    }

    X &operator++() {
        m_a++;
        m_b++;
        return *this;
    }

    X operator++(int) {
        X mem = *this;
        ++*this;
        return mem;
    }

    X operator+(const X& r) const {
        X new_obj = X(this->m_a + r.m_a, this->m_b + r.m_b);
        return new_obj;
    }

    std::string print(); // used for testing

    // overload << which is descripted not here have to know about object's fields which are private
    friend std::ostream& operator<<(std::ostream& os, const X& right);
};

std::string X::print() {
    std::stringstream s;
    s << "[" << m_a << "," << m_b << "]";
    return s.str();
}

// this func is not in class X because we have to know about std::ostream
std::ostream& operator<<(std::ostream& stream, const X& obj) {
    return stream << "[" << obj.m_a << "," << obj.m_b << "]";
}


/*  Expected:
 *  Use cases of the class that are expected to be working.
*/
/*
  { X{}                   } // see the logs, find out what happens with m_a and m_b
    { X{1,2};				    	  }
  { X{1};				       	  } // see the logs, find out what happens with m_b
    { X x{}, y{x};			  	}
    { X x{}, y = x;				  } // as expected
    { X x{}, y{}; y += x;		}
    { X x{}; ++x;				    }  // ++m_a, ++m_b
    { X x{}; x++;				    }  // m_a++, m_b++
    { X x{}, y{}; x + y;		}  // as expected
    { cout << X{1,2};				}  // shows "[1,2]"
*/
