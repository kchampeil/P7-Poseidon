package com.nnk.springboot;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BidTests {

    //TOASK renommer en BidListTest ? et déplacer dans dossier repository ? ne teste pas le service => plutôt changer les tests pour tester le service ?

    @Autowired
    private BidListRepository bidListRepository;


    @Test
    public void bidListTest() {
        //KC BidList bid = new BidList("Account Test", "Type Test", 10d);
        BidList bid = new BidList();
        bid.setAccount(TestConstants.NEW_BID_LIST_ACCOUNT);
        bid.setType(TestConstants.NEW_BID_LIST_TYPE);
        bid.setBidQuantity(TestConstants.NEW_BID_LIST_BID_QUANTITY);

        // Save
        //TOASK faire plus d'UT car là ne teste que le repository ? (ie = IT)
        bid = bidListRepository.save(bid);
        assertNotNull(bid.getBidListId());
        assertEquals(bid.getBidQuantity(), TestConstants.NEW_BID_LIST_BID_QUANTITY, TestConstants.NEW_BID_LIST_BID_QUANTITY);

        // KC TODO
        // Update
        bid.setBidQuantity(20d);
        bid = bidListRepository.save(bid);
        Assert.assertEquals(bid.getBidQuantity(), 20d, 20d);

        // Find
        List<BidList> listResult = bidListRepository.findAll();
        assertTrue(listResult.size() > 0);

		//KC TODO
		// Delete
		Integer id = bid.getBidListId();
		bidListRepository.delete(bid);
		Optional<BidList> bidList = bidListRepository.findById(id);
		Assert.assertFalse(bidList.isPresent());
    }
}
