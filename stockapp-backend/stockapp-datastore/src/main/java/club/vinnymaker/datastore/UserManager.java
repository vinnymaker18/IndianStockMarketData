package club.vinnymaker.datastore;

import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import club.vinnymaker.data.User;

public class UserManager {
	// loadUser, createUser, updateUser, deleteUser.
	private static UserManager userMgr = null;
	private UserManager() {
	}
	
	public static UserManager getInstance() {
		if (userMgr == null) {
			userMgr = new UserManager();
		}
		return userMgr;
	}
	
	private String createRandomSalt() {
		// TODO(vinay) -> Implement this.
		return "sat";
	}
	
	private String getPasswordHash(String password, String salt) {
		// TODO(vinay) -> Implement this.
		return "has";
	}
	
	public User loadUser(long userId) {
		Session session = DataStoreManager.getInstance().getFactory().openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			User user = (User) session.get(User.class, userId);
			tx.commit();
			return user;
		} catch(HibernateException e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return null;
	}
	
	public boolean updateUser(User user) {
		// TODO(vinay) -> Implement this.
		return false;
	}
	
	public boolean deleteUser(Long userId) {
		// TODO(vinay) -> Implement this.
		User user = loadUser(userId);
		if (user == null) {
			return false;
		}
		
		Session session = DataStoreManager.getInstance().getFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(user);
			tx.commit();
			return true;
		} catch (HibernateException e) {
			// TODO(vinay) -> Error deleting the user, must be logged.
			if (tx != null) {
				tx.rollback();
			}
			return false;
		} finally {
			if (session != null) {
				session.close();
			}
		}		
	}
	
	public Long createUser(String username, String password) {
		Session session = DataStoreManager.getInstance().getFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			User user = new User();
			String salt = createRandomSalt();
			String hash = getPasswordHash(password, salt);
			
			user.setUsername(username);
			user.setPasswordHash(hash);
			user.setPasswordSalt(salt);
			user.setDateCreated(new Date(System.currentTimeMillis()));
			Long id = (Long) session.save(user);
			tx.commit();
			return id;
		} catch (HibernateException e) {
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return null;
	}
}
