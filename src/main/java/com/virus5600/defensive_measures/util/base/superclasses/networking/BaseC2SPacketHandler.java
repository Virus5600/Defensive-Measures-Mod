package com.virus5600.defensive_measures.util.base.superclasses.networking;

/**
 * Base class for all C2S packet handlers.
 * <br><br>
 * This class provides a base template for all client-to-server packet
 * handlers, allowing for easy creation and management of packet handlers.
 * It is imperative to extend this class when creating a new C2S packet
 * so that helper methods and other features can be used, along with
 * code standardization.
 *
 * @since 1.1.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public abstract class BaseC2SPacketHandler {
	public void handle() {
	}
}
