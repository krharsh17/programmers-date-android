package in.krharsh17.programmersdate;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public interface Constants {

    int mapDefaultHeight = 14;
    String TAG = "DEFAULT";

    String sharedPrefName = "PDate";

    LatLng tennisCourt = new LatLng(25.620952, 85.172708);
    LatLng mainBuilding = new LatLng(25.620737, 85.171954);
    LatLng libraryNescafe = new LatLng(25.620737, 85.171581);
    LatLng library = new LatLng(25.620599, 85.171568);
    LatLng incubationCentre = new LatLng(25.620677, 85.171150);
    LatLng chemistryDept = new LatLng(25.620662, 85.171000);
    LatLng guestHouse = new LatLng(25.620691, 85.172888);
    LatLng cseDept = new LatLng(25.620621, 85.174219);
    LatLng nurseryShed = new LatLng(25.619992, 85.174031);
    LatLng cwrs = new LatLng(25.619617, 85.173964);
    LatLng sportsGround = new LatLng(25.620058, 85.172481);
    LatLng sac = new LatLng(25.619323, 85.172189);
    LatLng nescafeSac = new LatLng(25.619038, 85.171942);
    LatLng canteneLibrary = new LatLng(25.620438, 85.171719);
    LatLng canteneMainBuilding = new LatLng(25.621083, 85.171681);
    LatLng carParking = new LatLng(25.620933, 85.171512);
    LatLng mainGate = new LatLng(25.620307, 85.171928);
    LatLng cellTower = new LatLng(25.620655, 85.173690);

    LatLngBounds NITP_BOUNDS = new LatLngBounds(new LatLng(25.619097, 85.170036),
            new LatLng(25.621264, 85.175347));


    String helpTextPose = "Strike the given pose together with your partner and get it captured to ace this round!";
    String helpTextQR = "Find a hidden QR code in the marked areas!";
    String helpTextBar = "Find a hidden bar code in the show areas!";
    String helpTextLogo = "Find a popular logo in the set areas!";

    String taskTypeLogo = "LOGO";
    String taskTypeQR = "QR";
    String taskTypeBar = "BAR";
    String taskTypePose = "POSE";
    String taskTypeTwister = "TWISTER";
}
