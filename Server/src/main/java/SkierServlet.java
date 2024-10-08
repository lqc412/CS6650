import com.google.gson.Gson;
import entity.LiftRide;
import entity.ResponseMsg;
import entity.SkierVertical;
import entity.VerticalElement;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * SkierServlet is a servlet that handles HTTP GET and POST requests for skier data.
 * It processes requests related to skier lift rides and vertical information.
 */
@WebServlet(name = "SkierServlet", value = "/skiers/*")
public class SkierServlet extends HttpServlet {

    /**
     * Handles the HTTP GET request.
     * Based on the URL path, it validates the request and returns skier vertical information
     * or a simple "it works" message.
     *
     * @param req  The HttpServletRequest object that contains the request.
     * @param res  The HttpServletResponse object that contains the response.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an input or output error is detected when the servlet handles the GET request.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();
        Gson gson = new Gson();

        // Check if the URL is missing or empty
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write(gson.toJson(new ResponseMsg("Missing Parameter")));
            return;
        }

        String[] urlParts = urlPath.split("/");

        // Validate URL path and return response status
        if (!isUrlValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            res.setStatus(HttpServletResponse.SC_OK);

            // Process URL parts if valid
            if (urlParts.length == 3) {
                // Example of returning skier vertical information
                List<VerticalElement> vrl = new ArrayList<>();
                vrl.add(new VerticalElement("string", 32));
                SkierVertical skierVertical = new SkierVertical(vrl);
                res.getWriter().write(gson.toJson(skierVertical));
            } else {
                // Return simple "it works" message for other cases
                res.getWriter().write("it works");
            }
        }
    }

    /**
     * Handles the HTTP POST request.
     * Processes skier lift ride information from the URL and returns a 201 Created response on success,
     * or a 400 Bad Request response if there is an error.
     *
     * @param req  The HttpServletRequest object that contains the request.
     * @param res  The HttpServletResponse object that contains the response.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an input or output error is detected when the servlet handles the POST request.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();

        // Check if the URL is missing or empty
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing parameters");
            return;
        }

        String[] urlParts = urlPath.split("/");

        // Validate URL path and return response status
        Gson gson = new Gson();
        if (!isUrlValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ResponseMsg msg = new ResponseMsg("NOT FOUND");
            res.getWriter().write(gson.toJson(msg));
        } else {
            try {
                // Process and create a dummy LiftRide object for demonstration
                LiftRide liftRide = new LiftRide(217, 21, 5000);
                ResponseMsg msg = new ResponseMsg("Successful Created");
                String msgJsonString = gson.toJson(msg);
                PrintWriter out = res.getWriter();
                out.print(msgJsonString);
                out.flush();
                res.setStatus(HttpServletResponse.SC_CREATED);
            } catch (Exception ex) {
                // Handle error and return 400 response
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ResponseMsg msg = new ResponseMsg("Failed Created");
                String msgJsonString = gson.toJson(msg);
                PrintWriter out = res.getWriter();
                out.print(msgJsonString);
                out.flush();
            }
        }
    }

    /**
     * Validates the URL path to ensure it matches the expected format and contains valid values.
     *
     * @param urlPath A String array representing the parts of the URL path.
     * @return boolean - true if the URL path is valid, false otherwise.
     */
    private boolean isUrlValid(String[] urlPath) {
        // Example URL path: "/1/seasons/2019/days/1/skiers/123"
        if (urlPath.length == 3) {
            // If the length is 3, verify if the second part is a valid number and contains "vertical"
            return urlPath[1].chars().allMatch(Character::isDigit) && urlPath[2].contains("vertical");
        } else if (urlPath.length == 8) {
            // Validate that the URL has all necessary parts and they are properly formatted
            return urlPath[1].chars().allMatch(Character::isDigit) && urlPath[2].equals("seasons") &&
                    urlPath[3].chars().allMatch(Character::isDigit) && urlPath[4].equals("days") &&
                    urlPath[5].chars().allMatch(Character::isDigit) && urlPath[6].equals("skiers") &&
                    urlPath[7].chars().allMatch(Character::isDigit) && Integer.parseInt(urlPath[5]) >= 1 &&
                    Integer.parseInt(urlPath[5]) <= 365;
        }
        return false;
    }
}
