package me.sun.springjpacourse.repository;

import me.sun.springjpacourse.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void asd() throws Exception{
        //given
        Item  item = new Item();
        item.setCreatedDate(LocalDateTime.now());

        //when

        //then
    }


    @Test
    public void save() throws Exception {


        // Item item = new Item();
        /*
            현재까진 item은 null이다.
            SimpleJpaRepository의 save를 디버깅을 해보면 isNew가 true가 되므로 정상적으로 동작한다.
            @GeneratedValue는 persist때 들어간다.
         */
        // itemRepository.save(item);


        /*
            이렇게 item id를 @GeneratedValue로 만들지않고 직접만들면
            디버깅해보면 persist가아닌 merge를 하게 된다.
            이렇게 되면 일단 select query로 id가 A인 Item을 찾아보고 없으면 insert하게된다.
            비효율적이게 된다.

            실무에서 @GeneratedValue를 쓰지 못할때가 있다.
            그럴때는 Persistable Interface를 사용해야한다.
         */
        Item item = new Item("A");

        itemRepository.save(item);
    }


}