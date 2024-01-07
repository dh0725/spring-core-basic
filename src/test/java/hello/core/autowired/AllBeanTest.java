package hello.core.autowired;

import hello.core.AutoAppConfig;
import hello.core.discount.DiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class AllBeanTest {
    @Test
    void findAllBean() {
        // 스프링 컨테이너를 생성하면서 AutoAppConfig 와 DiscountService 를 스프링 빈으로 자동 등록한다.
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);

        DiscountService discountService = ac.getBean(DiscountService.class);
        Member member = new Member(1L, "userA", Grade.VIP);
        int fixDiscountPrice = discountService.discount(member, 10000, "fixDiscountPolicy");

        assertThat(discountService).isInstanceOf(DiscountService.class);
        assertThat(fixDiscountPrice).isEqualTo(1000);

        int rateDisCountPrice = discountService.discount(member, 20000, "rateDiscountPolicy");

        assertThat(rateDisCountPrice).isEqualTo(2000);
    }

    static class DiscountService {
        /*
        Map 으로 모든 DiscountPolicy 를 주입받는다. fixDiscountPolicy, rateDiscountPolicy 가 주입된다.
            Map 의 Key 에 스프링 빈의 이름을 넣어주고
            value 에 모든 DiscountPolicy 타입으로 조회된 모든 스프링 빈을 담는다.
         */
        private final Map<String, DiscountPolicy> policyMap;
        /*
        DiscountPolicy 타입으로 조회한 모든 스프링 빈을 담아준다.
        만약 해당하는 타입의 스프링 빈이 없으면 빈 컬렉션이나 Map 을 주입한다.
         */
        private final List<DiscountPolicy> policies;

        DiscountService(Map<String, DiscountPolicy> policyMap, List<DiscountPolicy> policies) {
            this.policyMap = policyMap;
            this.policies = policies;

            System.out.println("policyMap = " + policyMap);
            System.out.println("policies = " + policies);
        }

        // discoutnCode 로 fixDiscountPolicy 가 넘어오면 Map 에서 fixDiscountPolicy 빈을 찾아 실행하고
        // rateDiscountPolicy 가 넘어오면 Map 에서 rateDiscountPolicy 빈을 찾아 실행한다.
        public int discount(Member member, int price, String discountCode) {
            DiscountPolicy discountPolicy = policyMap.get(discountCode);

            return discountPolicy.discount(member, price);
        }
    }
}
