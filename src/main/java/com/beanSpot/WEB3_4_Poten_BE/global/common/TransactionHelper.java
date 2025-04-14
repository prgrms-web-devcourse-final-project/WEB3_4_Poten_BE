package com.beanSpot.WEB3_4_Poten_BE.global.common;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/**
 * 트랜잭션 처리를 도와주는 유틸리티 클래스
 *
 * @author -- 김남우 --
 * @since -- 4월 12일 --
 */
@Component
public class TransactionHelper {

    private static TransactionTemplate transactionTemplate;

    public TransactionHelper(TransactionTemplate template) {
        transactionTemplate = template;
    }

    public static <R> R execute(Supplier<R> block) {
        return transactionTemplate.execute(status -> {
            try {
                return block.get();
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }
}