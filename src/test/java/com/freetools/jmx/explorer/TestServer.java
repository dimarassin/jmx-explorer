package com.freetools.jmx.explorer;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.URIUtil;

/**
 * Date: 1/18/14
 *
 * @author Dima Rassin
 */
public class TestServer {
	public static void main(String[] args) throws Exception
	{
		Server server = new Server(8888);

		ContextHandler context = new ContextHandler();
		context.setContextPath(URIUtil.SLASH);

		ServletHandler servlet = new ServletHandler();
		servlet.addServletWithMapping(ExplorerServlet.class, "/jmx/*");
		context.setHandler(servlet);
		server.addHandler(context);

		server.start();
		server.join();
	}
}
