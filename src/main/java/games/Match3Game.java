package games;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 需要在初始化棋盘时确保没有初始的三消组合，并且至少有一个潜在的三消组合是通过鼠标拖动可以触发的。 初始化网格：随机生成彩色方块填充网格。
 * 鼠标事件处理：允许玩家点击并拖动方块交换位置。 有效交换检测：只允许相邻方块交换。 匹配检测：检测水平和垂直方向的三个或更多相同颜色的方块。
 * 消除匹配方块：消除匹配的方块，并让上方的方块掉落以填补空缺。 重新填充网格：随机生成新方块填补空缺。
 * 
 * @author Kevin
 *
 */
public class Match3Game extends JPanel implements MouseListener, MouseMotionListener {
	private static final int ROWS = 8;
	private static final int COLS = 8;
	private static final int TILE_SIZE = 50;

	private Color[][] grid = new Color[ROWS][COLS];
	private Point selectedTile = null;
	private Point dragTile = null;
	
    private int score = 0; // 记分变量


	public Match3Game() {
		initializeGrid();
		while (hasInitialMatches()) {
			initializeGrid();
		}
		ensureAtLeastOneMove();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	private void initializeGrid() {
		Random rand = new Random();
		Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE };

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				grid[row][col] = colors[rand.nextInt(colors.length)];
			}
		}
	}

	private boolean hasInitialMatches() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS - 2; col++) {
				if (grid[row][col] == grid[row][col + 1] && grid[row][col] == grid[row][col + 2]) {
					return true;
				}
			}
		}
		for (int col = 0; col < COLS; col++) {
			for (int row = 0; row < ROWS - 2; row++) {
				if (grid[row][col] == grid[row + 1][col] && grid[row][col] == grid[row + 2][col]) {
					return true;
				}
			}
		}
		return false;
	}

	private void ensureAtLeastOneMove() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (canTriggerMatch(row, col)) {
					return;
				}
			}
		}
		// If no moves found, force a move by swapping two tiles
		swapTiles(new Point(0, 0), new Point(0, 1));
	}

	private boolean canTriggerMatch(int row, int col) {
		Color temp = grid[row][col];
		if (row > 0 && row < ROWS - 1) {
			swapTiles(new Point(col, row), new Point(col, row - 1));
			if (hasInitialMatches()) {
				swapTiles(new Point(col, row), new Point(col, row - 1));
				return true;
			}
			swapTiles(new Point(col, row), new Point(col, row - 1));
		}
		if (col > 0 && col < COLS - 1) {
			swapTiles(new Point(col, row), new Point(col - 1, row));
			if (hasInitialMatches()) {
				swapTiles(new Point(col, row), new Point(col - 1, row));
				return true;
			}
			swapTiles(new Point(col, row), new Point(col - 1, row));
		}
		return false;
	}

	/**
	 * 特别关注最后一行和最后一列的绘制
	 */
	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    
	    // Clear the background
	    g.setColor(Color.WHITE);
	    g.fillRect(0, 0, getWidth(), getHeight());

	    // Draw tiles
	    for (int row = 0; row < ROWS; row++) {
	        for (int col = 0; col < COLS; col++) {
	            int x = col * TILE_SIZE;
	            int y = row * TILE_SIZE;
	            
	            // Fill tile with color
	            g.setColor(grid[row][col]);
	            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
	            
	            // Draw tile border
	            g.setColor(Color.BLACK);
	            g.drawRect(x, y, TILE_SIZE - 1, TILE_SIZE - 1);
	        }
	    }
	    
        // 绘制分数
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, getHeight() - 20);
	}

	private boolean isValidSwap(Point p1, Point p2) {
		return (Math.abs(p1.x - p2.x) == 1 && p1.y == p2.y) || (Math.abs(p1.y - p2.y) == 1 && p1.x == p2.x);
	}

	private void swapTiles(Point p1, Point p2) {
		Color temp = grid[p1.y][p1.x];
		grid[p1.y][p1.x] = grid[p2.y][p2.x];
		grid[p2.y][p2.x] = temp;
	}

	/**
	 * 在 checkMatches 方法中进行递归调用，直到没有新的三消组合可以消除为止
	 * 
	 * checkMatches
	 */
	private void checkMatches() {
		boolean[][] toRemove = new boolean[ROWS][COLS];

		// Check horizontal matches
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS - 2; col++) {
				if (grid[row][col] != null && grid[row][col] == grid[row][col + 1]
						&& grid[row][col] == grid[row][col + 2]) {
					toRemove[row][col] = true;
					toRemove[row][col + 1] = true;
					toRemove[row][col + 2] = true;
				}
			}
		}

		// Check vertical matches
		for (int col = 0; col < COLS; col++) {
			for (int row = 0; row < ROWS - 2; row++) {
				if (grid[row][col] != null && grid[row][col] == grid[row + 1][col]
						&& grid[row][col] == grid[row + 2][col]) {
					toRemove[row][col] = true;
					toRemove[row + 1][col] = true;
					toRemove[row + 2][col] = true;
				}
			}
		}

		// Remove matched tiles
		boolean foundMatches = false;
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (toRemove[row][col]) {
					grid[row][col] = null;
					foundMatches = true;
                    score += 10; // 每消除一个方块得10分

				}
			}
		}

		// Drop tiles
		if (foundMatches) {
			// Drop tiles
			for (int col = 0; col < COLS; col++) {
				for (int row = ROWS - 1; row >= 0; row--) {
					if (grid[row][col] == null) {
						for (int k = row - 1; k >= 0; k--) {
							if (grid[k][col] != null) {
								grid[row][col] = grid[k][col];
								grid[k][col] = null;
								break;
							}
						}
					}
				}
			}

			// Refill grid
			Random rand = new Random();
			Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE };
			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLS; col++) {
					if (grid[row][col] == null) {
						grid[row][col] = colors[rand.nextInt(colors.length)];
					}
				}
			}

			// Recursive call to check for new matches
			checkMatches();
		}

		repaint();
	}
	

	public void mousePressed(MouseEvent e) {
		int col = e.getX() / TILE_SIZE;
		int row = e.getY() / TILE_SIZE;
		if (col < COLS && row < ROWS) {
			selectedTile = new Point(col, row);
		}
	}

	public void mouseReleased(MouseEvent e) {
		int col = e.getX() / TILE_SIZE;
		int row = e.getY() / TILE_SIZE;
		if (col < COLS && row < ROWS && selectedTile != null) {
			dragTile = new Point(col, row);
			if (isValidSwap(selectedTile, dragTile)) {
				swapTiles(selectedTile, dragTile);
				checkMatches();
			}
			selectedTile = null;
			dragTile = null;
		}
	}

	public void mouseDragged(MouseEvent e) {
		// Not used
	}

	public void mouseMoved(MouseEvent e) {
		// Not used
	}

	public void mouseClicked(MouseEvent e) {
		// Not used
	}

	public void mouseEntered(MouseEvent e) {
		// Not used
	}

	public void mouseExited(MouseEvent e) {
		// Not used
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Match 3 Game");
		Match3Game gamePanel = new Match3Game();
		frame.add(gamePanel);
		
	    // 设置窗口大小为棋盘大小加上一些额外空间
	    int windowWidth = COLS * TILE_SIZE + 20; // 加上额外的空间
	    int windowHeight = ROWS * TILE_SIZE + 40; // 加上额外的空间
	    
	    frame.setSize(windowWidth, windowHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}