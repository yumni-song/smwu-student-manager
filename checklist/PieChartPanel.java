package checklist;

import javax.swing.*;
import java.awt.*;

public class PieChartPanel extends JPanel {
    private int completed = 0;
    private int total = 1; // 최소값 1로 설정하여 0 나눔 방지

    public void updateData(int completed, int total) {
        this.completed = completed;
        this.total = Math.max(total, 1);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = Math.min(getWidth(), getHeight()) - 20;
        int x = (getWidth() - width) / 2;
        int y = (getHeight() - width) / 2;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double percent = (double) completed / total;
        int angle = (int) (360 * percent);

        g2.setColor(new Color(102, 204, 255)); // 완료 부분
        g2.fillArc(x, y, width, width, 90, -angle);

        g2.setColor(new Color(224, 224, 224)); // 미완료 부분
        g2.fillArc(x, y, width, width, 90 - angle, -(360 - angle));

        g2.setColor(Color.DARK_GRAY);
        String text = String.format("%.0f%%", percent * 100);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2.drawString(text, getWidth() / 2 - textWidth / 2, getHeight() / 2 + textHeight / 4);
    }
}

