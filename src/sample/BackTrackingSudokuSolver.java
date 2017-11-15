package sample;

import java.util.Arrays;

public class BackTrackingSudokuSolver implements ISudokuSolver {

    public static final int SUDOKU_SIZE = 9; //rows and columns
    public static final int BLOCK_SIZE = 3; //blocks
    public static final int EMPTY_CELL = 0;

    /**
     * Solves the given Sudoku using backtracking (finds all possible solutions)
     * @param sudoku unsolved sudokusolve
     */

    private int[][] solvedSudoku = new int[9][9];

    @Override
    public int[][] solve(int[][] sudoku) {
        boolean[][] initialSudoku = new boolean[SUDOKU_SIZE][SUDOKU_SIZE];

        for (int i = 0; i < SUDOKU_SIZE; i++)
            for (int j = 0; j < SUDOKU_SIZE; j++) {
                if(sudoku[i][j] > 0)
                    initialSudoku[i][j] = true;
            }

        int x = -1;
        int y = 0;
        do {
            x++; //shift to the next cell;
            if (x >= SUDOKU_SIZE) {
                x = 0;
                y += 1;
            }
        } while (initialSudoku[y][x]);

        for (int i = 1; i <= SUDOKU_SIZE; i++) {
            solve(sudoku, initialSudoku, x, y, i);
        }

        return solvedSudoku;
    }
    /**
     * Solves the Sudoku
     * @param solution solution
     * @param initialSudoku solution, where true denotes that the value is in the initial setting, false
     * that its a member of the partial solution
     * @param x x coordinate, where the next decision will be made
     * @param y y coordinate, where the next decision will be made
     * @param value decision
     */
    private void solve(int[][] solution, boolean[][] initialSudoku, int x, int y, int value) {
        if (!validateCurrentSudoku(solution, x, y, value)) return; //the solution is not valid
        solution[y][x] = value; //set
        do {
            x = x + 1; //shift to the next cell
            boolean overflow = x >= SUDOKU_SIZE; //row overflow?
            if (overflow) {
                x = 0;
                y += 1;
                if (y == SUDOKU_SIZE ) { //column overflow?...solution is complete
                    for (int i = 0; i < solution.length; i++)
                        solvedSudoku[i] = Arrays.copyOf(solution[i], solution[i].length);
                    return;
                }
            }
        } while (initialSudoku[y][x]); //while the field is initialSudoku (part of the initial setting)
        for (int i = 1; i <= SUDOKU_SIZE; i++) { //backtrack
            solve(solution, initialSudoku, x, y, i);
        }
        solution[y][x] = EMPTY_CELL; //reset the cell (otherwise it would infere with the backtracking algorithm)
    }
    /**
     * Checks, if the partial solution is consistent
     * @param sudoku partial solution
     * @param x x coordinate, where the decision was made
     * @param y y coordinate, where the decision was made
     * @param value decision
     * @return true - if the partial solution is consistent, false - if it is not consistent
     */
    private static boolean validateCurrentSudoku(int[][] sudoku, int x, int y, int value) {
        //column
        for (int i = 0; i < sudoku.length; i++) {
            if (i != y && sudoku[i][x] == value) return false;
        }
        //row
        for (int i = 0; i < sudoku[y].length; i++) {
            if (i != x && sudoku[y][i] == value) return false;
        }
        //block
        int vertical = y/BLOCK_SIZE; //vertical row index
        int horizontal = x/BLOCK_SIZE; //horizontal row index

        for (int i = vertical*BLOCK_SIZE; i < vertical*BLOCK_SIZE + BLOCK_SIZE; i++) {
            for (int j = horizontal*BLOCK_SIZE; j < horizontal*BLOCK_SIZE + BLOCK_SIZE; j++) {
                if (sudoku[i][j] == value) return false;
            }
        }
        return true;
    }
}
