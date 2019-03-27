package de.oemel09.lsf.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LsfApi {

    String ASI = "asi";
    String COOKIE = "Cookie";
    String NODE_ID = "nodeID";

    String PERFORM_LOGIN = "rds?state=user&type=1&category=auth.login&startpage=portal.vm&breadCrumbSource=portal";
    String GET_ASI_TOKEN = "rds?state=change&type=1&moduleParameter=studyPOSMenu&nextdir=change&next=menu.vm&subdir=applications&xml=menu&purge=y&navigationPosition=functions%2CstudyPOSMenu&breadcrumb=studyPOSMenu&topitem=functions&subitem=studyPOSMenu";
    String GET_GRADES_QUERY = "rds?state=notenspiegelStudent&next=list.vm&nextdir=qispos/notenspiegel/student&createInfos=Y&struct=auswahlBaum&nodeID=auswahlBaum%7Cabschluss%3Aabschl%3D84%2Cstgnr%3D1&expand=0";
    String GET_GRADE_DETAILS = "rds?state=notenspiegelStudent&next=list.vm&nextdir=qispos/notenspiegel/student&createInfos=Y&struct=abschluss&expand=0";

    @FormUrlEncoded
    @POST(PERFORM_LOGIN)
    Call<ResponseBody> performLogin(@FieldMap Map<String, String> lsfLogin);

    @POST(GET_ASI_TOKEN)
    Call<ResponseBody> loadAsiToken(@Header(COOKIE) String sessionId);

    @POST(GET_GRADES_QUERY)
    Call<ResponseBody> loadGrades(@Header(COOKIE) String sessionId,
                                  @Query(ASI) String asi);

    @GET(GET_GRADE_DETAILS)
    Call<ResponseBody> loadGradeDetails(@Header(COOKIE) String sessionId,
                                        @Query(ASI) String asi,
                                        @Query(NODE_ID) String nodeId);
}
