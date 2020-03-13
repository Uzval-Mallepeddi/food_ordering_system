package com.foodOrderingSystem.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodOrderingSystem.entity.Cart;
import com.foodOrderingSystem.entity.FoodItem;
import com.foodOrderingSystem.service.FoodItemService;

@Controller
public class MenuController {
	
	@Autowired
	private FoodItemService foodItemService;
	
	HashMap<Integer, Integer> cartItems = new HashMap<Integer, Integer>();
	List<Cart> showCartItems = new ArrayList<Cart>();

	@GetMapping("/menu")
	public String showMenu(Model theModel, HttpServletRequest request) {
		HttpSession session = request.getSession();
		List<FoodItem> foodItems = foodItemService.getAllFoodItems();
		session.setAttribute("cartItems", cartItems);
		theModel.addAttribute("foodItems", foodItems);
		return "menu";
	}
	
	@GetMapping("/cart")
	public String showCart(Model theModel) {
		int totalQuantity = 0;
		double totalPrice = 0;
		
		for(Cart item: showCartItems) {
			System.out.println(item);
		}
		Boolean flag = false;
		for (Integer item : cartItems.keySet())
		{
			flag = false;
			FoodItem foodItem = foodItemService.getFoodItem(item);
			for(Cart cartItem: showCartItems) {
				if(item == cartItem.getId()) {
					flag=true;
					totalQuantity += cartItems.get(item);
					totalPrice += (foodItem.getPrice() * cartItems.get(item));
					totalPrice = Math.round(totalPrice);
					break;
				}
				else
				{
					flag = false;
					continue;
				}
			}
			if(!flag)
			{
				Cart cart = new Cart();
				cart.setId(foodItem.getId());
				cart.setName(foodItem.getName());
				cart.setDesc(foodItem.getDesc());
				cart.setImglink(foodItem.getImglink());
				cart.setPrice(foodItem.getPrice());
				cart.setQuantity(cartItems.get(item));
				totalQuantity += cartItems.get(item);
				totalPrice += (foodItem.getPrice() * cartItems.get(item));
				totalPrice = (double) Math.round(totalPrice*100.0)/100.0;
				showCartItems.add(cart);
			}
		}
		for(Cart item: showCartItems) {
			System.out.println(item);
		}
		System.out.println("-------");
		theModel.addAttribute("showCartItems", showCartItems);
		theModel.addAttribute("quantity", totalQuantity);
		theModel.addAttribute("price", totalPrice);
		return "cart";
	}
	
	@PostMapping("/menu/addToCart")
	public @ResponseBody void renderData(@RequestBody String search) {
		int itemId = Integer.valueOf(search.split("&")[0].split("=")[1]);
		int itemQuantity = Integer.valueOf(search.split("&")[1].split("=")[1]);
		if(cartItems.containsKey(itemId)) {
			cartItems.remove(itemId);
		}
		else {
			cartItems.put(itemId, itemQuantity);
		}
		
		for (Map.Entry<Integer,Integer> item : cartItems.entrySet())
            System.out.println("FoodItemId = " + item.getKey() + ", Quantity = " + item.getValue());
		
	}
	
	@PostMapping("/menu/RemoveFromCart/{id}")
	public String removeItem(@PathVariable int id) {
		System.out.println("Show Cart Items-->"+showCartItems);
		for(Cart cart: showCartItems) {
			if(cart.getId() == id) {
				showCartItems.remove(cart);
				break;
			}
		}
		cartItems.remove(id);
		return "redirect:/cart";
	}
}
