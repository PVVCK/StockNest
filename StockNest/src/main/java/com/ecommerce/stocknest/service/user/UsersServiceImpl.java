package com.ecommerce.stocknest.service.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.stocknest.dto.AddUsersDTO;
import com.ecommerce.stocknest.dto.FetchUsersDTO;
import com.ecommerce.stocknest.dto.UsersDTO;
import com.ecommerce.stocknest.model.Cart;
import com.ecommerce.stocknest.model.CartItem;
import com.ecommerce.stocknest.model.Users;
import com.ecommerce.stocknest.repository.CartRepository;
import com.ecommerce.stocknest.repository.UserRepository;
import com.ecommerce.stocknest.service.cart.cartitem.CartItemServiceImpl;

import jakarta.transaction.Transactional;

@Service
public class UsersServiceImpl implements UsersService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private CartItemServiceImpl cartItemServiceImpl;
	
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public UsersDTO createUser(AddUsersDTO addUserDTO) {
		// TODO Auto-generated method stub
		Users user = modelMapper.map(addUserDTO, Users.class);

		userRepository.save(user);
		
		return modelMapper.map(user, UsersDTO.class);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public UsersDTO updateUser(AddUsersDTO users, Long usersId) {
		// TODO Auto-generated method stub
		 Users existingUser = userRepository.findById(usersId)
		            .orElseThrow(() -> new NoSuchElementException("User with Id :- " + usersId + " is not found"));
		    
		    
		    if (users.getUsername() != null) {
		        existingUser.setUsername(users.getUsername());
		    }
		    if (users.getEmail() != null) {
		        existingUser.setEmail(users.getEmail());
		    }
		    if (users.getPhoneNumber() != null) {
		        existingUser.setPhoneNumber(users.getPhoneNumber());
		    }
		    
		    Users updatedUser = userRepository.save(existingUser);
		    
		   
		    return modelMapper.map(updatedUser, UsersDTO.class);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public UsersDTO getUserByID(Long usersId) {
		// TODO Auto-generated method stub
		Users users = userRepository.findById(usersId)
				.orElseThrow(() -> new NoSuchElementException("User with Id :- "+usersId+" is not found"));
		
		return modelMapper.map(users, UsersDTO.class);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public List<FetchUsersDTO> getAllUsers() {
		// TODO Auto-generated method stub
		List<Users> usersList = userRepository.findAll();

		if (usersList.isEmpty()) {
	        throw new NoSuchElementException("No Users are available in the database.");
	    }
	    // Use ModelMapper to convert the list of Users to a list of UsersDTO
	    List<FetchUsersDTO> usersDTOList = usersList.stream()
	            .map(user -> modelMapper.map(user, FetchUsersDTO.class))
	            .toList();

	    return usersDTOList;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void deleteUser(Long usersId) {
		// TODO Auto-generated method stub
		userRepository.findById(usersId)
		.ifPresentOrElse(userRepository::delete,
				() -> {throw new NoSuchElementException("User with Id:- " +usersId +" is not Present");});
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public Cart createCartForUser(Long userId) {
		// TODO Auto-generated method stub
		 Users user = userRepository.findById(userId)
	                .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " not found."));

		 
		 if (!user.getCarts().isEmpty()) {
		        // Return the first cart associated with the user
		        Cart existingCart = user.getCarts().get(0); // Assuming a user has only one cart
		        return modelMapper.map(existingCart, Cart.class);
		    }
		 
	        // Create a new cart and associate with the user
	        Cart newCart = new Cart();
	        newCart.setUsers(user);
	        user.getCarts().add(newCart);

	        // Save the cart
	        Cart savedCart = cartRepository.save(newCart);

	        // Map and return the response as CartDTO
	        return modelMapper.map(savedCart, Cart.class);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public CartItem addCartItemFromUser(Long userId, Long productId, int quantity) {
		// TODO Auto-generated method stub
		Cart cart = cartRepository.findByUsers_UserId(userId)
				.orElseThrow(() -> new NoSuchElementException("No Carts Associated with User Id:-"+ userId));
		return cartItemServiceImpl.addCartItem(cart.getCartId(), productId, quantity);
	}

}
