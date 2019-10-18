package pl.koszela.spring.DAOs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.koszela.spring.entities.Tiles;
import pl.koszela.spring.repositories.TilesRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DaoTiles implements Dao {

    private final TilesRepository tilesRepository;

    @Autowired
    public DaoTiles(TilesRepository tilesRepository) {
        this.tilesRepository = Objects.requireNonNull(tilesRepository);
    }

    @Override
    public final void save(String filePath, String priceListName) {
        String line;
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                Tiles tiles = new Tiles();
                tiles.setName(data[1]);
                tiles.setDiscount(0);
                tiles.setPriceDetalUnit(new BigDecimal(Double.parseDouble(data[2])));
                tiles.setPriceFromRepo(Double.valueOf(data[3]));
                tiles.setBasicDiscount(Integer.valueOf(data[4]));
                tiles.setAdditionalDiscount(Integer.valueOf(data[5]));
                tiles.setPromotionDiscount(Integer.valueOf(data[6]));
                tiles.setSkontoDiscount(Integer.valueOf(data[7]));
                tiles.setPriceListName(priceListName);
                tilesRepository.save(tiles);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("GeneratePdfReport nie tak save EntityTiles in ProductDAo");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}