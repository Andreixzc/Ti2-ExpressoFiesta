package service;

import java.util.List;

import dao.LocalDAO;
import model.Local;
import spark.Request;
import spark.Response;

public class LocalService {

    private LocalDAO localDAO = new LocalDAO();

    private void setReponseHeaders(Response response) {
		response.header("Content-Type", "application/json");
		response.header("Content-Encoding", "UTF-8");
		response.header("Access-Control-Allow-Credentials", "true");
        response.header("Access-Control-Allow-Origin", "*");
        response.header("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, PATCH");
        response.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Authorization, "
                + "Content-Type, Accept, X-CSRF-TOKEN, Cache-Control, DNT, X-CustomHeader, Keep-Alive, "
                + "User-Agent, If-Modified-Since, Content-Range, Range");
        response.header("Access-Control-Max-Age", "3600");
        response.type("application/json");
	}

    private Local buscarPorId(Request request) {
        int id = Integer.parseInt(request.params(":id"));
        return localDAO.buscar(id);
    }

    public Local get(Request request, Response response) {
        setReponseHeaders(response);
        Local local = buscarPorId(request);

        if (local != null) {
            response.status(200);
            return local;
        } else {
            response.status(404);
            return null;
        }
    }

    public List<Local> getAll(Request request, Response response) {
        setReponseHeaders(response);
        return localDAO.listar();
    }

    public Local insert(Request request, Response response) {
        setReponseHeaders(response);

        String endereco = request.queryParams("endereco");
        String nome = request.queryParams("nome");
        String status = request.queryParams("status");
        float valor = Float.parseFloat(request.queryParams("valor"));

        Local local = new Local(-1, endereco, nome, status, valor);

        if (localDAO.insert(local)) {
            response.status(201);
            return local;
        } else {
            response.status(404);
            return null;
        }
    }

    public Local update(Request request, Response response) {
        setReponseHeaders(response);
        Local local = buscarPorId(request);
        if (local != null) {
            local.setEndereco(request.queryParams("endereco"));
            local.setNome(request.queryParams("nome"));
            local.setStatus(request.queryParams("status"));
            local.setValor(Float.parseFloat(request.queryParams("valor")));
            response.status(200);
            return local;
        } else {
            response.status(404);
            return null;
        }

    }

    public String delete(Request request, Response response) {
        Local local = buscarPorId(request);

        if (local != null) {
            localDAO.delete(local.getId());
            response.status(200);
            return "Local de ID " + local.getId() + " excluido!";
        } else {
            response.status(404);
            return "Local nao encontrado";
        }
    }

}
	
	
