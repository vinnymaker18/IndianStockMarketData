package club.vinnymaker.appfrontend;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bigtesting.routd.NamedParameterElement;
import org.bigtesting.routd.Route;
import org.bigtesting.routd.Router;
import org.bigtesting.routd.TreeRouter;
import org.json.JSONObject;

import club.vinnymaker.appfrontend.controllers.UserController;
import club.vinnymaker.appfrontend.controllers.IController;
import club.vinnymaker.appfrontend.controllers.StockController;
import lombok.Getter;

public class RoutingServlet extends HttpServlet {
	private static final long serialVersionUID = 956003895642907877L;
	private static Router router;
	
	public static final String ERROR_CODE_KEY = "errorCode";
	public static final String INCORRECT_REQUEST_METHOD_ERROR = "Wrong method for this request";
	public static final String RESOURCE_NOT_FOUND_ERROR = "Resource not found";
	
	@Getter
	static class APIRoute extends Route {
		private final String requestMethod;
		private final IController controller;
		
		public APIRoute(String path, String method,  IController controller) {
			super(path);
			requestMethod = method;
			this.controller = controller;
		}
	}
	
	// All valid URIs are registered here in the router. 
	static {
		router = new TreeRouter();
		
		// User data related requests.
		router.add(new APIRoute("/users/:username", "GET", UserController::getUser));
		router.add(new APIRoute("/users", "POST", UserController::createOrUpdateUser));
		
		// Stock data related requests.
		router.add(new APIRoute("/stocks/:exchange/:symbol", "GET", StockController::getItemData));
		router.add(new APIRoute("/stocks/:exchange/:symbol/members", "GET", StockController::getIndexComponents));
		router.add(new APIRoute("/stocks/search/:substr", "GET", StockController::getMatches));
		
		// Exchange related requests.
		router.add(new APIRoute("/exchanges/:exids", "GET", StockController::getExchanges));
		router.add(new APIRoute("/exchanges/:exid/indexes", "GET", StockController::getIndexes));
	}
	
	private static String errorResponseBody(String errorReason) {
		JSONObject obj = new JSONObject();
		obj.put(ERROR_CODE_KEY, errorReason);
		return obj.toString();
	}
	
	public static void sendError(HttpServletResponse resp, int statusCode, String reason) throws IOException {
		resp.setStatus(statusCode);
		PrintWriter pw = resp.getWriter();
		pw.write(errorResponseBody(reason));
		pw.close();
	}
	
	private static boolean checkRequestAndThrow(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Route route = router.route(req.getPathInfo());
		if (!(route instanceof APIRoute)) {
			sendError(resp, HttpServletResponse.SC_NOT_FOUND, RESOURCE_NOT_FOUND_ERROR);
			return false;
		}
		
		APIRoute aroute = (APIRoute) route;
		if (!aroute.getRequestMethod().equals(req.getMethod())) {
			sendError(resp, HttpServletResponse.SC_FORBIDDEN, INCORRECT_REQUEST_METHOD_ERROR);
			return false;
		}
		
		return true;
	}
	
	/** 
	 * Returns a dictionary of named parameters in the URI mapped to their values. e.g., for the endpoint
	 *  /stocks/search/:symbol and a matching request /stocks/search/CEMENT, it returns
	 *  the map {symbol : CEMENT}.
	 */
	private static Map<String, String> getVariableParams(Route route, String path) {
		Map<String, String> ret = new HashMap<>();
		for (NamedParameterElement elem : route.getNamedParameterElements()) {
			String value = route.getNamedParameter(elem.name(), path);
			ret.put(elem.name(), value);
		}
		return ret;
	}
	
	// Does some sanity checking, set some response headers and call the controller method.
	private static void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Content-Type", "application/json");
		if (!checkRequestAndThrow(req, resp)) {
			return;
		}
		
		APIRoute route = (APIRoute) router.route(req.getPathInfo());
		route.getController().view(req, resp, getVariableParams(route, req.getPathInfo()));
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}
}
