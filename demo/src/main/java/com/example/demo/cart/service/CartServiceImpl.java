package com.example.demo.cart.service;

import com.example.demo.account.entity.Account;
import com.example.demo.account.repository.AccountRepository;
import com.example.demo.cart.entity.CartItem;
import com.example.demo.cart.controller.request.CartToOrderRequest;
import com.example.demo.order.controller.response.OrderResponse;
import com.example.demo.order.entity.Order;
import com.example.demo.order.repository.OrderRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.bread.entity.Bread;
import com.example.demo.bread.repository.BreadRepository;
import com.example.demo.cart.controller.request.CartRequest;
import com.example.demo.cart.controller.response.CartResponse;
import com.example.demo.cart.entity.Cart;
import com.example.demo.cart.repository.CartRepository;
import com.example.demo.cart.repository.CartItemRepository;
import com.example.demo.utils.CheckToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor

public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AccountRepository accountRepository;
    private final BreadRepository breadRepository;
    private final OrderRepository orderRepository;
    private final CheckToken checkToken;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CartResponse>> getAllCart() {
        List<Cart> carts = cartRepository.findAllWithItemsAndBread();
        List<CartResponse> response = carts.stream()
                .flatMap(cart -> cart.getCartItems().stream()
                        .map(cartItem -> CartResponse.builder()
                                .id(cartItem.getId())
                                .quantity(cartItem.getQuantity())
                                .bread(cartItem.getBread())
                                .account(cart.getAccount())
                                .build()))
                .collect(Collectors.toList());
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<CartResponse> addCart(String token, CartRequest request) {

        Long accountId = Long.parseLong(checkToken.findAccountId(token));

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("계정을 찾을 수 없습니다."));
        Long breadId = request.getBreadId();
        Bread bread = breadRepository.findById(breadId).orElseThrow(() -> new RuntimeException("빵을 찾을 수 없습니다."));

        Cart cart = cartRepository.findByAccountIdWithItems(accountId)
                .orElse(Cart.builder()
                        .account(account)
                        .cartItems(new ArrayList<>())
                        .build());

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getBread() != null && item.getBread().getId().equals(breadId))
                .findFirst()
                .orElse(null);

        CartItem cartItem;
        if (existingItem != null) {
            existingItem.addQuantity(request.getQuantity());
            cartItem = existingItem;
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .bread(bread)
                    .quantity(request.getQuantity())
                    .build();
            cart.getCartItems().add(cartItem);
        }

        cartRepository.save(cart);

        CartResponse response = CartResponse.builder().id(cartItem.getId()).quantity(cartItem.getQuantity()).account(account).bread(bread).build();

        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<OrderResponse> cartToOrder(String token, CartToOrderRequest request) {
        Long accountId = Long.parseLong(checkToken.findAccountId(token));

        CartItem cartItem = cartItemRepository.findByIdWithBreadAndCart(request.getCartItemId())
                .orElseThrow(() -> new RuntimeException("장바구니 아이템을 찾을 수 없습니다."));

        if (!cartItem.getCart().getAccount().getId().equals(accountId)) {
            return ApiResponse.failure("접근 권한이 없습니다.");
        }

        Order order = Order.builder()
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getBread().getPrice() * cartItem.getQuantity())
                .isReserved(request.isReserved())
                .account(cartItem.getCart().getAccount())
                .bread(cartItem.getBread())
                .build();

        Order savedOrder = orderRepository.save(order);
        cartItemRepository.delete(cartItem);

        OrderResponse response = OrderResponse.builder()
                .id(savedOrder.getId())
                .quantity(savedOrder.getQuantity())
                .totalPrice(savedOrder.getTotalPrice())
                .createdAt(savedOrder.getCreatedAt())
                .isReserved(savedOrder.isReserved())
                .accountId(savedOrder.getAccount().getId())
                .breadId(savedOrder.getBread().getId())
                .build();

        return ApiResponse.success(response);
    }
}
