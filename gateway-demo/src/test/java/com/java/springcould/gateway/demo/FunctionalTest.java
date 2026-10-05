package com.java.springcould.gateway.demo;

import com.java.springcould.gateway.demo.domain.Product;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.*;

public class FunctionalTest {

    public static void main(String[] args) {
        Product product = new Product();

        Consumer<Product> consumer1 = new Consumer<Product>() {
            @Override
            public void accept(Product product) {
                Product.nameOf(product);
            }
        };
        consumer1.accept(product);

        Consumer<Product> consumer2 = Product::nameOf;
        consumer2.accept(product);

        Function<Integer, Integer> function = product::reduceStock;
        System.out.println("剩余库存：" + function.apply(10));

        Function<Integer, Product> function1 = Product::new;
        System.out.println("剩余库存：" + function1.apply(200));

        Supplier<Integer> supplier = product::reduceStock;
        System.out.println("剩余库存：" + supplier.get());

        Predicate<Integer> predicate = new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return product.isEnough(integer);
            }
        };

        System.out.println("是否充足：" + predicate.test(10));

        Predicate<Integer> predicate1 = product::isEnough;
        System.out.println("是否充足：" + predicate1.test(100));

        UnaryOperator<Integer> integerUnaryOperator = product::reduceStock;
        System.out.println("剩余库存：" + integerUnaryOperator.apply(20));

        IntUnaryOperator intUnaryOperator = product::reduceStock;
        System.out.println("剩余库存：" + intUnaryOperator.applyAsInt(20));

        Supplier<Product> supplier1 = Product::new;
        System.out.println("supplier1：" + supplier1.get());

        Supplier<Product> supplier2 = () -> new Product();
        System.out.println("supplier2：" + supplier2.get());

        BiFunction<Product, Integer, Integer> biFunction = Product::reduceStock;
        System.out.println("剩余库存(BiFunction)：" + biFunction.apply(product, 20));

        BinaryOperator<Integer> binaryOperator = product::reduceStock;
        System.out.println("剩余库存(BinaryOperator)：" + binaryOperator.apply(product.getStock(), 20));

        BinaryOperator<Product> binaryOperator1 = BinaryOperator.minBy(Comparator.comparingInt((Product product1) -> product1.getStock()));
        Product minProduct = binaryOperator1.apply(product, supplier2.get());
        System.out.println("minProduct：" + minProduct);

        BinaryOperator<Product> binaryOperator2 = BinaryOperator.maxBy(Comparator.comparingInt((Product product1) -> product1.getStock()));
        Product maxProduct = binaryOperator2.apply(product, supplier2.get());
        System.out.println("maxProduct：" + maxProduct);

        product.setName(null);

        String name = Optional.of(product.getName()).orElseGet(() -> "默认名称");
        System.out.println("名称：" + name);
    }
}
