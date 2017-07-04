package midiplayer.frame;

import javax.swing.table.TableModel;

/**
 * Table model which handles row movements.
 *
 * @see <a href="http://stackoverflow.com/questions/638807/how-do-i-drag-and-drop-a-row-in-a-jtable"
 *      >How do I drag and drop a row in a JTable?</a>
 */
public interface ReorderableTableModel extends TableModel {

  /**
   * Moves one or more rows from the inclusive range start to end to the to position in the model.
   *
   * <p>
   * After the move, the row that was at index start will be at index to. This method will send a
   * tableChanged notification message to all the listeners.
   * </p>
   *
   * @param start the starting row index to be moved
   * @param end the ending row index to be moved
   * @param to the destination of the rows to be moved
   *
   * @throws IndexOutOfBoundsException if an endpoint index value is out of range
   *         ({@code start < 0 || end > size})
   * @throws IllegalArgumentException if the endpoint indices are out of order
   *         ({@code fromIndex > toIndex})
   */
  public void moveRow(int start, int end, int to);

  /**
   * Moves one row from the index to the to position in the model.
   *
   * <p>
   * After the move, the row that was at index start will be at index to. This method will send a
   * tableChanged notification message to all the listeners.
   * </p>
   *
   * @param index the row index to be moved
   * @param to the destination of the row to be moved
   *
   * @throws IndexOutOfBoundsException if an endpoint index value is out of range
   *         ({@code index < 0 || index > size})
   */
  default public void moveRow(int index, int to) {
    this.moveRow(index, index, to);
  }

}
