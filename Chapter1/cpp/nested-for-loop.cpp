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

  for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) cout << matrix[r][c] << " ";
    cout << "\n";
  }
}
