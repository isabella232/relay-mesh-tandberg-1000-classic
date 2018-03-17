package vc.bjn.catalyst.tandberg1000classicmesh.data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A video conferencing room system provisioned in Relay.
 * @see https://relay.bluejeans.com/docs/Endpoint.html
 */
@XmlRootElement
public class Endpoint {

    private String name;
    private String listenerServiceId;
    private String ipAddress;
    private Integer port;
    private String controlProtocol;
    private String signalingProtocol;
    private String dialStyle;
    private String addressStyle;
    private String calendarId;
    private String calendarType;
    private String username;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getListenerServiceId() {
        return listenerServiceId;
    }

    public void setListenerServiceId(final String listenerServiceId) {
        this.listenerServiceId = listenerServiceId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public String getControlProtocol() {
        return controlProtocol;
    }

    public void setControlProtocol(final String controlProtocol) {
        this.controlProtocol = controlProtocol;
    }

    public String getSignalingProtocol() {
        return signalingProtocol;
    }

    public void setSignalingProtocol(final String signalingProtocol) {
        this.signalingProtocol = signalingProtocol;
    }

    public String getDialStyle() {
        return dialStyle;
    }

    public void setDialStyle(final String dialStyle) {
        this.dialStyle = dialStyle;
    }

    public String getAddressStyle() {
        return addressStyle;
    }

    public void setAddressStyle(final String addressStyle) {
        this.addressStyle = addressStyle;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(final String calendarId) {
        this.calendarId = calendarId;
    }

    public String getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(final String calendarType) {
        this.calendarType = calendarType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((addressStyle == null) ? 0 : addressStyle.hashCode());
        result = prime * result + ((calendarId == null) ? 0 : calendarId.hashCode());
        result = prime * result + ((calendarType == null) ? 0 : calendarType.hashCode());
        result = prime * result + ((controlProtocol == null) ? 0 : controlProtocol.hashCode());
        result = prime * result + ((dialStyle == null) ? 0 : dialStyle.hashCode());
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + ((listenerServiceId == null) ? 0 : listenerServiceId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((signalingProtocol == null) ? 0 : signalingProtocol.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Endpoint other = (Endpoint) obj;
        if (addressStyle == null) {
            if (other.addressStyle != null)
                return false;
        } else if (!addressStyle.equals(other.addressStyle))
            return false;
        if (calendarId == null) {
            if (other.calendarId != null)
                return false;
        } else if (!calendarId.equals(other.calendarId))
            return false;
        if (calendarType == null) {
            if (other.calendarType != null)
                return false;
        } else if (!calendarType.equals(other.calendarType))
            return false;
        if (controlProtocol == null) {
            if (other.controlProtocol != null)
                return false;
        } else if (!controlProtocol.equals(other.controlProtocol))
            return false;
        if (dialStyle == null) {
            if (other.dialStyle != null)
                return false;
        } else if (!dialStyle.equals(other.dialStyle))
            return false;
        if (ipAddress == null) {
            if (other.ipAddress != null)
                return false;
        } else if (!ipAddress.equals(other.ipAddress))
            return false;
        if (listenerServiceId == null) {
            if (other.listenerServiceId != null)
                return false;
        } else if (!listenerServiceId.equals(other.listenerServiceId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        if (signalingProtocol == null) {
            if (other.signalingProtocol != null)
                return false;
        } else if (!signalingProtocol.equals(other.signalingProtocol))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Endpoint [name=" + name + ", listenerServiceId=" + listenerServiceId + ", ipAddress=" + ipAddress + ", port="
                + port + ", controlProtocol=" + controlProtocol + ", signalingProtocol=" + signalingProtocol + ", dialStyle="
                + dialStyle + ", addressStyle=" + addressStyle + ", calendarId=" + calendarId + ", calendarType=" + calendarType
                + ", username=" + username + "]";
    }

}
