#include <iostream>
using namespace std;

int main() {
  int rows = 3;
  int cols = 3;
  int matrix[rows][cols] = {
    { 1, 2, 3 },
    { 4, 5, 6 },
    { 7, 8, 9 }
  };

  int r = 0;
  row_loop:
  if (r < rows) {
    int c = 0;
    col_loop:
    if (c < cols) {
      cout << matrix[r][c] << " ";
      c++;
      goto col_loop;
    }

    cout << "\n";

    r++;
    goto row_loop;
  }
 
  return 0;
}
