package gecko2.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

public class NumberInRectangle extends JLabel{

	private static final long serialVersionUID = -5779445240934635898L;
    private final NumberIcon icon;

    public NumberInRectangle(int number, Color bg) {
        this(number, bg, null);
    }

    public NumberInRectangle(int number, Color bg, MouseListener ml) {
		this.setBackground(bg);
        this.icon = new NumberIcon(number);

        this.setPreferredSize(new Dimension(icon.getIconWidth()+1, icon.getIconHeight()+1));
        this.setMaximumSize(new Dimension(icon.getIconWidth()+1, icon.getIconHeight()+1));
        this.setMinimumSize(new Dimension(icon.getIconWidth() + 1, icon.getIconHeight() + 1));

		if (ml!=null)
			this.addMouseListener(ml);

        this.setIcon(icon);
	}

    public static class NumberIcon implements Icon{
        private final int number;
        private final int width;
        private final int height;

        public NumberIcon(int number) {
            this.number = number;

            int length = Integer.toString(number).length();
            if (length == 1)
                this.width = 16;
            else
                this.width = 8 + 9 * (length - 1) - 1;

            this.height = 16;
        }

        /**
         * Draw the icon at the specified location.  Icon implementations
         * may use the Component argument to get properties useful for
         * painting, e.g. the foreground or background color.
         */
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.BLUE);

            g.drawRect(x, y, getIconWidth(), getIconHeight());

            g.setColor(Color.black);
            g.setFont(g.getFont().deriveFont(10.5F));
            if (number<10)
                g.drawString(Integer.toString(number), x+5, y+12);
            else
                g.drawString(Integer.toString(number), x+3, y+12);
        }

        /**
         * Returns the icon's width.
         *
         * @return an int specifying the fixed width of the icon.
         */
        @Override
        public int getIconWidth() {
            return width;
        }

        /**
         * Returns the icon's height.
         *
         * @return an int specifying the fixed height of the icon.
         */
        @Override
        public int getIconHeight() {
            return height;
        }

        /**
         * Returns the number the icon displays
         * @return the number that is displayed
         */
        public int getNumber() {
            return number;
        }
    }
}