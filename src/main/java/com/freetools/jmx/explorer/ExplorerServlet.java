package com.freetools.jmx.explorer;

import com.freetools.jmx.explorer.entities.commands.JmxGetAttribute;
import com.freetools.jmx.explorer.entities.commands.JmxGetTopology;
import org.codehaus.jackson.map.ObjectMapper;

import javax.management.JMException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Date: 1/17/14
 *
 * @author Dima Rassin
 */
public class ExplorerServlet extends HttpServlet {
	private ObjectMapper mapper;
	private Explorer explorer;

	@Override
	public void init() throws ServletException {
		explorer = new Explorer().init();
		new ExplorerAgent().register("locally");
		mapper = new ObjectMapper();
	}

	@Override
	public void destroy() {
		explorer.shutdown();
	}

	@Override
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo == null) res.sendError(HttpServletResponse.SC_BAD_REQUEST);
		else {
			String[] path = pathInfo.split("/");
			if (path.length < 2 || path.length > 3){
				res.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			res.setStatus(HttpServletResponse.SC_OK);
			res.setContentType("application/json");

			if ("nodes".equals(path[1])){
				getNodes(res);
				return;
			}

			// the first path element should be a node id
			if (!explorer.isNodeExist(path[1])){
				res.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			if (path.length == 2 && "POST".equals(req.getMethod())){
				//noinspection unchecked
				doInvoke(res, path[1], req.getParameterMap(), req.getInputStream());
				return;
			}

			if (path.length == 3 && "GET".equals(req.getMethod())){
				//noinspection unchecked
				doGet(res, path[1], path[2], req.getParameterMap());
				return;
			}

			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private void getNodes(HttpServletResponse res) throws IOException {
		res.getWriter().print(mapper.writeValueAsString(explorer.getNodes()));
	}

	private void doInvoke(HttpServletResponse res, String nodeId, Map<String, String> parameterMap, ServletInputStream inputStream) {

	}

	private void doGet(HttpServletResponse res, String nodeId, String command, Map<String,String[]> parameters) throws IOException {
		try {
			switch (command.toLowerCase()){
				case "topology":
					res.getWriter().print(explorer.execute(nodeId, new JmxGetTopology()).getResult());
					break;
				case "attribute":
					JmxGetAttribute cmd = new JmxGetAttribute(parameters.get("objectName")[0], parameters.get("attributeName")[0]);
					res.getWriter().print(mapper.writeValueAsString(explorer.execute(nodeId, cmd).getResult()));
					break;
			}
		} catch (JMException ex) {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}
}
