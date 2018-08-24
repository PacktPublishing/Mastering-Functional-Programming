#include <iostream>
using namespace std;

int main() {
  int rows = 3;
  int cols = 3;
  int matrix[3][3] = {
    { 1, 2, 3 },
    { 4, 5, 6 },
    { 7, 8, 9 }
  };
  // memset( matrix, 0, rows*cols*sizeof(int) );
  // matrix 

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
