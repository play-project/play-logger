/**
 * 
 */
package org.ow2.play.logger.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebMethod;

import org.ow2.play.logger.api.Log;
import org.ow2.play.logger.api.LogService;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;

/**
 * 
 * @author chamerling
 * 
 */
public class MongoLogService implements LogService {

	private final static String DEFAULT_MONGO_DB_HOSTNAME = "localhost";
	private final static String DEFAULT_MONGO_DB_PORT = "27017";
	private final static String DEFAULT_MONGO_DB_DATABASE_NAME = "play";
	private final static String DEFAULT_MONGO_DB_COLLECTION_NAME = "logs";

	private String hostname = DEFAULT_MONGO_DB_HOSTNAME;
	private String port = DEFAULT_MONGO_DB_PORT;
	private String databaseName = DEFAULT_MONGO_DB_DATABASE_NAME;
	private String collectionName = DEFAULT_MONGO_DB_COLLECTION_NAME;
	private String userName;
	private String password;
	private Mongo mongo;
	private DBCollection collection;

	private Properties properties;

	boolean initialized = false;

	Logger logger = Logger.getLogger(MongoLogService.class.getName());

	/**
	 * To be called before all...
	 */
	public void init() {
		logger.info("Initializing metadata service");

		if (mongo != null) {
			close();
		}

		if (properties != null) {
			hostname = properties.getProperty("mongo.hostname",
					DEFAULT_MONGO_DB_HOSTNAME);
			port = properties.getProperty("mongo.port", DEFAULT_MONGO_DB_PORT);
			userName = properties.getProperty("mongo.username", userName);
			password = properties.getProperty("mongo.password", password);
			collectionName = properties.getProperty("mongo.collection",
					DEFAULT_MONGO_DB_COLLECTION_NAME);
		}

		if (logger.isLoggable(Level.INFO)) {
			logger.info(String.format(
					"Connection to %s %s with credentials %s %s", hostname,
					port, userName, "******"));
		}

		List<ServerAddress> addresses = getServerAddresses(hostname, port);
		mongo = getMongo(addresses);

		DB database = getDatabase(mongo, databaseName);

		if (userName != null && userName.trim().length() > 0) {
			if (!database.authenticate(userName, password.toCharArray())) {
				throw new RuntimeException(
						"Unable to authenticate with MongoDB server.");
			}

			// Allow password to be GCed
			password = null;
		}

		setCollection(database.getCollection(collectionName));
		initialized = true;
	}

	/*
	 * This method could be overridden to provide the DB instance from an
	 * existing connection.
	 */
	protected DB getDatabase(Mongo mongo, String databaseName) {
		return mongo.getDB(databaseName);
	}

	protected DBCollection getDbCollection() {
		return this.collection;
	}

	/*
	 * This method could be overridden to provide the Mongo instance from an
	 * existing connection.
	 */
	protected Mongo getMongo(List<ServerAddress> addresses) {
		if (addresses.size() == 1) {
			return new Mongo(addresses.get(0));
		} else {
			// Replica set
			return new Mongo(addresses);
		}
	}

	protected void close() {
		if (mongo != null) {
			collection = null;
			mongo.close();
		}
	}

	/**
	 * Note: this method is primarily intended for use by the unit tests.
	 * 
	 * @param collection
	 *            The MongoDB collection to use when logging events.
	 */
	public void setCollection(final DBCollection collection) {
		assert collection != null : "collection must not be null";

		this.collection = collection;
	}

	/**
	 * Returns a List of ServerAddress objects for each host specified in the
	 * hostname property. Returns an empty list if configuration is detected to
	 * be invalid, e.g.:
	 * <ul>
	 * <li>Port property doesn't contain either one port or one port per host</li>
	 * <li>After parsing port property to integers, there isn't either one port
	 * or one port per host</li>
	 * </ul>
	 * 
	 * @param hostname
	 *            Blank space delimited hostnames
	 * @param port
	 *            Blank space delimited ports. Must specify one port for all
	 *            hosts or a port per host.
	 * @return List of ServerAddresses to connect to
	 */
	private List<ServerAddress> getServerAddresses(String hostname, String port) {
		List<ServerAddress> addresses = new ArrayList<ServerAddress>();

		String[] hosts = hostname.split(" ");
		String[] ports = port.split(" ");

		if (ports.length != 1 && ports.length != hosts.length) {
			// errorHandler
			// .error("MongoDB appender port property must contain one port or a port per host",
			// null, ErrorCode.ADDRESS_PARSE_FAILURE);
		} else {
			List<Integer> portNums = getPortNums(ports);
			// Validate number of ports again after parsing
			if (portNums.size() != 1 && portNums.size() != hosts.length) {
				// error("MongoDB appender port property must contain one port or a valid port per host",
				// null, ErrorCode.ADDRESS_PARSE_FAILURE);
			} else {
				boolean onePort = (portNums.size() == 1);

				int i = 0;
				for (String host : hosts) {
					int portNum = (onePort) ? portNums.get(0) : portNums.get(i);
					try {
						addresses.add(new ServerAddress(host.trim(), portNum));
					} catch (UnknownHostException e) {
						// errorHandler
						// .error("MongoDB appender hostname property contains unknown host",
						// e, ErrorCode.ADDRESS_PARSE_FAILURE);
					}
					i++;
				}
			}
		}
		return addresses;
	}

	private List<Integer> getPortNums(String[] ports) {
		List<Integer> portNums = new ArrayList<Integer>();

		for (String port : ports) {
			try {
				Integer portNum = Integer.valueOf(port.trim());
				if (portNum < 0) {
					// errorHandler
					// .error("MongoDB appender port property can't contain a negative integer",
					// null, ErrorCode.ADDRESS_PARSE_FAILURE);
				} else {
					portNums.add(portNum);
				}
			} catch (NumberFormatException e) {
				// errorHandler
				// .error("MongoDB appender can't parse a port property value into an integer",
				// e, ErrorCode.ADDRESS_PARSE_FAILURE);
			}

		}

		return portNums;
	}

	@Override
	public void pushMessage(String message) {
		Log log = new Log();
		log.setContext(null);
		log.setDate("" + System.currentTimeMillis());
		log.setMessage(message);

		this.push(log);
	}

	@Override
	public List<Log> list() {
		List<Log> result = new ArrayList<Log>();
		DBCursor cursor = collection.find();
		Iterator<DBObject> iter = cursor.iterator();
		while (iter.hasNext()) {
			DBObject dbObject = iter.next();
			if (dbObject != null) {
				Log log = new Log();
				log.setContext(dbObject.get("context") == null ? "" : dbObject
						.get("context").toString());
				log.setDate(dbObject.get("date") == null ? "" : dbObject.get(
						"date").toString());
				log.setMessage(dbObject.get("message") == null ? "" : dbObject
						.get("message").toString());
				result.add(log);
			}
		}
		return result;
	}

	@Override
	public void clear() {
		// TODO
	}

	@Override
	public void push(Log log) {
		if (log == null) {
			return;
		}

		DBObject o = new BasicDBObject();
		o.put("context", log.getContext());
		o.put("message", log.getMessage());
		o.put("date", log.getDate());
		collection.insert(o);
	}
}
