#include <iostream>
using namespace std;

// main() is where program execution begins.
int main() {
  int x = 0;
  loop_start:
    x++;
    cout << x << "\n";
    if (x < 10) goto loop_start;
  return 0;
}
