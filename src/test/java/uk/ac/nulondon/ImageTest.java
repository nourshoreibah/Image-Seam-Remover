package uk.ac.nulondon;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;
import java.awt.Color;

public class ImageTest {

    @Test
    public void testCalcIndividE() throws Exception {
        LinkedImage testImage = new LinkedImage("src/main/resources/beach.png");
        testImage.calculateEnergies();
        Assertions.assertThat(testImage.head.getEnergy()).isEqualTo(126.33641157199646);
        Assertions.assertThat(testImage.head.down.getEnergy()).isEqualTo(239.55746237130202);
        Assertions.assertThat(testImage.head.getDownRight().getEnergy()).isEqualTo(239.55746237130202);
    }

    @Test
    public void testFindBluest() throws Exception {
        LinkedImage testImage = new LinkedImage("src/main/resources/beach.png");
        Assertions.assertThat(testImage.findBluest().getFirst().getColor()).isEqualTo(new Color(153,217,234));
        Assertions.assertThat(testImage.findBluest().getLast().getColor()).isEqualTo(new Color(77,109,243));
        Assertions.assertThat(testImage.findBluest().get(3).getColor()).isEqualTo(new Color(153,217,234));
        Assertions.assertThat(testImage.findBluest().get(1).getColor()).isEqualTo(new Color(255,255,255));
    }

    @Test
    void testFindLowestE() throws Exception {
        LinkedImage testImage = new LinkedImage("src/main/resources/beach.png");
        Assertions.assertThat(testImage.findLowestE().getFirst().getColor()).isEqualTo(new Color(153,217,234));
        Assertions.assertThat(testImage.findLowestE().getLast().getColor()).isEqualTo(new Color(77,109,243));
        Assertions.assertThat(testImage.findLowestE().get(3).getColor()).isEqualTo(new Color(153,217,234));
        Assertions.assertThat(testImage.findLowestE().get(1).getColor()).isEqualTo(new Color(255,255,255));
    }

    @Test
    void highlightSeamTest() throws Exception {
        LinkedImage testImage = new LinkedImage("src/main/resources/beach.png");

        java.util.List<PixelNode> seamBlue = testImage.findBluest();
        testImage.highlightSeam(seamBlue,Color.BLUE);
        for (PixelNode node : seamBlue) {
            Assertions.assertThat(node.getColor()).isEqualTo(Color.BLUE);
        }

        java.util.List<PixelNode> seamLowE = testImage.findLowestE();
        testImage.highlightSeam(seamLowE,Color.RED);
        for (PixelNode node : seamLowE) {
            Assertions.assertThat(node.getColor()).isEqualTo(Color.RED);
        }
    }

    @Test
    void removeSeamTest() throws Exception {
        LinkedImage testImage = new LinkedImage("src/main/resources/beach.png");
        int originalWidth = testImage.width;
        java.util.List<PixelNode> seamBlue = testImage.findBluest();
        testImage.removeSeam(seamBlue);
        int newWidth = testImage.width;
        Assertions.assertThat(originalWidth).isEqualTo(newWidth + 1);
    }

}

