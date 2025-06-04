import org.hdstart.MemberMain;
import org.hdstart.cloud.mapper.ImagesMapper;
import org.hdstart.cloud.vo.BlogImgUrlVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = MemberMain.class)
public class MemberMainTest {

    @Autowired
    ImagesMapper imagesMapper;

}
