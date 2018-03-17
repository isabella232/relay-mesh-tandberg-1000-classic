package vc.bjn.catalyst.tandberg1000classicmesh.data;

import java.util.Objects;

public class Meeting {

    private String meetingId;
    private String passcode;
    private String dialString;
    private String bridgeAddress;

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getDialString() {
        return dialString;
    }

    public void setDialString(String dialString) {
        this.dialString = dialString;
    }

    public String getBridgeAddress() {
        return bridgeAddress;
    }

    public void setBridgeAddress(String bridgeAddress) {
        this.bridgeAddress = bridgeAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
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
