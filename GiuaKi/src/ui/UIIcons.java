package ui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.Icon;

public class UIIcons {

    public static Icon createSearchIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.0f));

                int r = size * 5 / 16;
                int cx = x + size * 3 / 8 + 1;
                int cy = y + size * 3 / 8 + 1;

                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                g2.drawLine(cx + r - 1, cy + r - 1, x + size - 3, y + size - 3);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createPlusIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int cx = x + size / 2;
                int cy = y + size / 2;
                int len = size / 3;

                g2.drawLine(cx - len, cy, cx + len, cy);
                g2.drawLine(cx, cy - len, cx, cy + len);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createDotsIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);

                int r = 2;
                int cy = y + size / 2;
                g2.fillOval(x + size / 4 - r, cy - r, r * 2, r * 2);
                g2.fillOval(x + size / 2 - r, cy - r, r * 2, r * 2);
                g2.fillOval(x + size * 3 / 4 - r, cy - r, r * 2, r * 2);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createPhoneIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                Path2D p = new Path2D.Float();
                p.moveTo(x + 5, y + 8);
                p.curveTo(x + 5, y + 16, x + 10, y + 21, x + 18, y + 21);
                p.lineTo(x + 21, y + 18);
                p.lineTo(x + 17, y + 14);
                p.lineTo(x + 14, y + 16);
                p.curveTo(x + 11, y + 14, x + 10, y + 12, x + 9, y + 9);
                p.lineTo(x + 11, y + 7);
                p.lineTo(x + 8, y + 4);
                p.closePath();

                g2.draw(p);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createVideoIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);

                g2.fillRoundRect(x + 2, y + 7, 13, 11, 4, 4);

                Path2D p = new Path2D.Float();
                p.moveTo(x + 16, y + 10);
                p.lineTo(x + 22, y + 6);
                p.lineTo(x + 22, y + 19);
                p.lineTo(x + 16, y + 15);
                p.closePath();

                g2.fill(p);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createInfoIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.2f));

                g2.drawOval(x + 3, y + 3, size - 6, size - 6);
                g2.fillOval(x + size / 2 - 1, y + 7, 3, 3);
                g2.drawLine(x + size / 2, y + 12, x + size / 2, y + 18);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    // Modern Document File Icon (Folded Top-Right Corner)
    public static Icon createAttachIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int w = 14;
                int h = 18;
                int left = x + (size - w) / 2;
                int top = y + (size - h) / 2;
                int fold = 5;

                Path2D p = new Path2D.Float();
                p.moveTo(left, top);
                p.lineTo(left + w - fold, top);
                p.lineTo(left + w, top + fold);
                p.lineTo(left + w, top + h);
                p.lineTo(left, top + h);
                p.closePath();

                g2.draw(p);
                g2.drawLine(left + w - fold, top, left + w - fold, top + fold);
                g2.drawLine(left + w - fold, top + fold, left + w, top + fold);

                // Document text lines inside
                g2.drawLine(left + 3, top + 8, left + w - 3, top + 8);
                g2.drawLine(left + 3, top + 13, left + w - 5, top + 13);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createSendIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);

                Path2D p = new Path2D.Float();
                p.moveTo(x + 3, y + 4);
                p.lineTo(x + size - 2, y + size / 2);
                p.lineTo(x + 3, y + size - 4);
                p.lineTo(x + size * 3 / 8, y + size / 2);
                p.closePath();

                g2.fill(p);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createSunIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.0f));

                int cx = x + size / 2;
                int cy = y + size / 2;
                int r = size / 4;
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);

                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4;
                    int x1 = (int) (cx + (r + 2) * Math.cos(angle));
                    int y1 = (int) (cy + (r + 2) * Math.sin(angle));
                    int x2 = (int) (cx + (r + 5) * Math.cos(angle));
                    int y2 = (int) (cy + (r + 5) * Math.sin(angle));
                    g2.drawLine(x1, y1, x2, y2);
                }
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createMoonIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);

                Area moon = new Area(new Ellipse2D.Float(x + 2, y + 2, size - 4, size - 4));
                moon.subtract(new Area(new Ellipse2D.Float(x + 7, y + 1, size - 5, size - 5)));

                g2.fill(moon);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    // Server UI Icons
    public static Icon createPulseIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                Path2D p = new Path2D.Float();
                int cy = y + size / 2;
                p.moveTo(x + 2, cy);
                p.lineTo(x + size / 4, cy);
                p.lineTo(x + size * 3 / 8, cy - size / 3);
                p.lineTo(x + size / 2, cy + size / 3);
                p.lineTo(x + size * 5 / 8, cy - size / 4);
                p.lineTo(x + size * 3 / 4, cy);
                p.lineTo(x + size - 2, cy);

                g2.draw(p);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createPortIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f));

                g2.drawRoundRect(x + 2, y + 3, size - 4, size - 6, 4, 4);
                int cy = y + size / 2;
                g2.fillRect(x + 5, cy - 2, 2, 4);
                g2.fillRect(x + 9, cy - 2, 2, 4);
                g2.fillRect(x + 13, cy - 2, 2, 4);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createUsersIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f));

                int cx = x + size / 2;
                g2.drawOval(cx - 4, y + 2, 8, 8);
                Path2D body = new Path2D.Float();
                body.moveTo(cx - 8, y + 18);
                body.curveTo(cx - 8, y + 13, cx + 8, y + 13, cx + 8, y + 18);
                g2.draw(body);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createPinIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f));

                int cx = x + size / 2;
                g2.drawOval(cx - 5, y + 2, 10, 10);
                g2.fillOval(cx - 2, y + 5, 4, 4);
                Path2D p = new Path2D.Float();
                p.moveTo(cx - 4, y + 10);
                p.lineTo(cx, y + 19);
                p.lineTo(cx + 4, y + 10);
                g2.draw(p);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createPlayIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f));

                g2.drawOval(x + 1, y + 1, size - 2, size - 2);

                Path2D p = new Path2D.Float();
                p.moveTo(x + size / 3 + 2, y + size / 4 + 1);
                p.lineTo(x + size * 3 / 4, y + size / 2);
                p.lineTo(x + size / 3 + 2, y + size * 3 / 4 - 1);
                p.closePath();

                g2.fill(p);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createStopIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f));

                g2.drawOval(x + 1, y + 1, size - 2, size - 2);
                g2.drawRoundRect(x + size / 3, y + size / 3, size / 3, size / 3, 2, 2);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createTrashIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f));

                g2.drawLine(x + 3, y + 5, x + size - 3, y + 5);
                g2.drawLine(x + size / 3, y + 3, x + size * 2 / 3, y + 3);
                g2.drawRoundRect(x + 5, y + 5, size - 10, size - 7, 3, 3);
                g2.drawLine(x + 8, y + 9, x + 8, y + size - 5);
                g2.drawLine(x + size - 8, y + 9, x + size - 8, y + size - 5);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }
}
