package com.innovationchef;

import com.innovationchef.constant.BatchConstant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("default")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTests {

    @Autowired
    @Qualifier("payJob")
    private Job payJob;

    @Autowired
    @Qualifier("custJob")
    private Job custJob;

    @Test
    public void test() {
        Assert.assertNotNull(payJob);
        Assert.assertNotNull(custJob);
        Assert.assertEquals(BatchConstant.PAY_JOB_NAME, payJob.getName());
        Assert.assertEquals(BatchConstant.CUST_JOB_NAME, custJob.getName());
    }
}
