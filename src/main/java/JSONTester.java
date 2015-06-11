import org.json.JSONObject;


public class JSONTester {

	public static void main(String[] args) {
		String testJSON = new String("{\"id\":\"https://login.salesforce.com/id/00D24000000Yws2EAC/00524000000rFfhAAE\",\"issued_at\":\"1434029113842\",\"scope\":\"id full custom_permissions api visualforce openid web refresh_token chatter_api\",\"instance_url\":\"https://eu5.salesforce.com\",\"token_type\":\"Bearer\",\"refresh_token\":\"5Aep861rz1k6fS7SfeicKQocWFljqEg1mhJEMQyV1HRjCQufgg9Bxlc5vVBnhAUdb.vGVkn04FeW16J9NLblFy2\",\"id_token\":\"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjE5NiJ9.eyJleHAiOjE0MzQwMjkyMzMsInN1YiI6Imh0dHBzOi8vbG9naW4uc2FsZXNmb3JjZS5jb20vaWQvMDBEMjQwMDAwMDBZd3MyRUFDLzAwNTI0MDAwMDAwckZmaEFBRSIsImF0X2hhc2giOiI5RmhQcklEcTI2OTBBdkxTZTJQVEFnIiwiYXVkIjoiM01WRzlSZDNxQzZvTWFsVVFtUko5Z1ZiYmNkN2ZhOXZtQVJDNUNKZTdXenpQZ0Q5RHlfZFFhWVlCQ0JyN1owQjh2TEZaNzFiaG5HTXJnaHZIRERZdSIsImlzcyI6Imh0dHBzOi8vbG9naW4uc2FsZXNmb3JjZS5jb20iLCJpYXQiOjE0MzQwMjkxMTN9.VWIu61S91dEbAvjehxEDhOJySxdITfdwc5fAMaqWmTRPSRPrVrFYAO4jj6eLU-IZ9s5yldNpAJi3ZFz12Kl3Qzu0jSyIxPSLQu2mXpqF7n_1_u077LoROMHM-IKgxlGHj9sEZCAaWLTe91ySKsbydfXzIaWC0B_nBTVq-0ddxttKvFmbv7VdjkTHUp9sDwkdRLOTtPc6CGoN-4CHgt7l9hoDnwk7pPEfi673NAryQrUSdnuiMU3Dz5m-Z_5OI-nN8AzCaqhaVeGz57SnnOOjHZ9Q4CXOa3uB1QYy1JKlGudgA317Rcti30Dqt1vfOnM2hZKkCiVmtJEtgimeai0qk6UWZZys2V4figyj4z9U5_Xd-DlHbYxY8lY3I5KycTOafYhJ6_9iTvSiR6OyiLsBM4XT41cRSBFlyD4aVCbEFE7wpmoVo05WT5KwbEgDLU9dtVyFqgbp5ueiAzWmcDBnPUox3XeCgZCFyGOqQtj_sDx2xf5-bDeZABZ9JY-PctjeXw6A02gY_mvJ7ldtouNfTpxNy5EBW4Bj9PZZR84M8xYoKkMMNtDaFRNq0LovSMXRiV-stVWkuef2G6nVC02Z9hSUvr-lwlpWWDgWxgP9S5liyip28frH8hK-cdByKvvlAXR4EA-cAYbiS2MiKHuROiClh0x6BmX6msGZ7CYgILk\",\"signature\":\"ko2RB/wSZIqDoqu1VinQq/hlf37v0zHXgVb3IFif4yw=\",\"access_token\":\"00D24000000Yws2!AQ4AQM.m6VWEKSC8gv3glp3PyZx4jUCuSgHKXJiXb_MfZ.NCWv0ZRvOoW2D25x9Cf9Cnv1vOAQ_LqAPTELs0rifaLdzXjoh3\"}");
		JSONObject jsonObject = new JSONObject(testJSON);
		System.out.println(jsonObject.toString());
	}

}
