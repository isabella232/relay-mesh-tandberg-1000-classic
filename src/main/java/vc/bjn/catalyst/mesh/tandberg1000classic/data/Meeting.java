package vc.bjn.catalyst.mesh.tandberg1000classic.data;

import java.util.Objects;

public class Meeting {

    private String meetingId;
    private String passcode;
    private String dialString;
    private String bridgeAddress;

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(final String meetingId) {
        this.meetingId = meetingId;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(final String passcode) {
        this.passcode = passcode;
    }

    public String getDialString() {
        return dialString;
    }

    public void setDialString(final String dialString) {
        this.dialString = dialString;
    }

    public String getBridgeAddress() {
        return bridgeAddress;
    }

    public void setBridgeAddress(final String bridgeAddress) {
        this.bridgeAddress = bridgeAddress;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Meeting meeting = (Meeting) o;
        return Objects.equals(meetingId, meeting.meetingId) &&
                Objects.equals(passcode, meeting.passcode) &&
                Objects.equals(dialString, meeting.dialString) &&
                Objects.equals(bridgeAddress, meeting.bridgeAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meetingId, passcode, dialString, bridgeAddress);
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "meetingId='" + meetingId + '\'' +
                ", passcode='" + passcode + '\'' +
                ", dialString='" + dialString + '\'' +
                ", bridgeAddress='" + bridgeAddress + '\'' +
                '}';
    }
}
