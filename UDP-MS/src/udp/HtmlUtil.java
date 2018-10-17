package udp;

import java.util.List;

public class HtmlUtil {

	public static String formatAsTableResponse(List<String> resp) {
		StringBuilder sb = new StringBuilder();

		sb.append("<!DOCTYPE html><html><body><table border=\"1\">\n");

		String hdr = resp.get(0);

		String[] data = hdr.split(",");
		sb.append("<tr>");
		for (String h : data) {
			sb.append("<th>").append(h).append("</th>");
		}
		sb.append("</tr>\n");

		for (int i = 1; i < resp.size(); i++) {

			String row = resp.get(i);

			data = row.split(",");
			sb.append("<tr>");
			for (String h : data) {
				sb.append("<td>").append(h).append("</td>");
			}
			sb.append("</tr>\n");
		}
		
		sb.append("</table></body></html>\n");

		return sb.toString();

	}

}
