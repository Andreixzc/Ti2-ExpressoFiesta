package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Alimento;


import model.AlimentoQuantidade;


import model.Atracao;

import model.Pedido;

public class PedidoDAO extends DAO {

    private final String SELECT_QUERY = "SELECT * FROM PEDIDO";

    private final String SELECT_QUERY_ATRACAO = "SELECT ATRACAO.*, PEDIDO_ATRACAO.pedido_id "
			+ "FROM PEDIDO "
			+ "INNER JOIN PEDIDO_ATRACAO ON (PEDIDO.ID = PEDIDO_ATRACAO.PEDIDO_ID) "
			+ "INNER JOIN ATRACAO ON (ATRACAO.ID = PEDIDO_ATRACAO.ATRACAO_ID)";

	private final String SELECT_QUERY_ALIMENTO = "SELECT ALIMENTO.*, PEDIDO_ALIMENTO.pedido_id, PEDIDO_ALIMENTO.quantidade pedido_alimento_quantidade"
           + " FROM PEDIDO "
           + " INNER JOIN PEDIDO_ALIMENTO ON (PEDIDO.ID = PEDIDO_ALIMENTO.PEDIDO_ID) "
           + " INNER JOIN ALIMENTO ON (ALIMENTO.ID = PEDIDO_ALIMENTO.ALIMENTO_ID) " ;

	public PedidoDAO() {
		super();
		conectar();
	}

	public void finalize() {
		close();
	}

	public boolean insert(Pedido pedido) {

		boolean status = false;
		try {
			String sql = "INSERT INTO pedido (data_pedido,total,local_id,usuario_id) VALUES ('"
					+ pedido.getData_pedido() + "'," + pedido.getTotal() + "," + pedido.getLocal_id() + ","
					+ pedido.getUsuario_id() + ")";//Inseri dados do pedodo na tabela
			
			PreparedStatement st = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			st.executeUpdate();
            int idPedido = 0;
            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                idPedido = generatedKeys.getInt("id");
            }

			for (Atracao atracao : pedido.getAtracoes()) {
				String sql2 = "INSERT INTO pedido_atracao (pedido_id, atracao_id) VALUES ('" + idPedido + "',"
						+ atracao.getId()  + ")";
				PreparedStatement st2 = conexao.prepareStatement(sql2);
				st2.executeUpdate();
				st2.close();//pega todas as atracoes do pedido e inseri na tabela pedido_atracao
			}

			for (AlimentoQuantidade alimentoQuantidade : pedido.getAlimentosQuantidade()) {
                String sql3 = "INSERT INTO pedido_alimento (quantidade, alimento_id, pedido_id) VALUES (" + alimentoQuantidade.getQuantidade() + ","
                        + alimentoQuantidade.getAlimento().getId() + "," + idPedido + ")";
                PreparedStatement st3 = conexao.prepareStatement(sql3);
                st3.executeUpdate();
                st3.close();//pega todas os alimentos do pedido e inseri na tabela pedido_alimento
            }

			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

	public boolean delete(int id) {
		boolean status = false;

		try {
			String sql = "DELETE from pedido where id = " + id;
			PreparedStatement st = conexao.prepareStatement(sql);
			st.executeUpdate();
			st.close();
			status = true;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
		return status;
	}

	public List<Pedido> listar() {
		List<Pedido> retorno = new ArrayList<>();
		try {
			PreparedStatement st = conexao.prepareStatement(SELECT_QUERY);
			ResultSet resultado = st.executeQuery();
			while (resultado.next()) {
				Pedido pedido = criarPedido(resultado);
				retorno.add(pedido);
			}
			st.close();

            PreparedStatement stAtracao = conexao.prepareStatement(SELECT_QUERY_ATRACAO);
            resultado = stAtracao.executeQuery();
            while (resultado.next()) {
				Pedido pedido = getPedidoById(retorno, resultado.getInt("pedido_id"));
                pedido.getAtracoes().add(criarAtracao(resultado));
            }
            stAtracao.close();

            PreparedStatement stAlimento = conexao.prepareStatement(SELECT_QUERY_ALIMENTO);
            resultado = stAlimento.executeQuery();
            while (resultado.next()) {
                Pedido pedido = getPedidoById(retorno, resultado.getInt("pedido_id"));
                pedido.getAlimentosQuantidade().add(criarAlimentoQuantidade(resultado));
            }
            stAlimento.close();

		} catch (Exception e) {
			System.out.println("Erro ao listar: " + e);
		}
		return retorno;
	}

	public Pedido buscar(int id) {
		try {
			Pedido pedido = new Pedido();
			PreparedStatement st = conexao.prepareStatement(SELECT_QUERY + " where PEDIDO.id = " + id);
			ResultSet resultado = st.executeQuery();
			while (resultado.next()) {
				pedido = criarPedido(resultado);
			}
			st.close();

			PreparedStatement stAtracao = conexao.prepareStatement(SELECT_QUERY_ATRACAO + " where PEDIDO.id = " + id);
			resultado = stAtracao.executeQuery();
			while (resultado.next()) {
				pedido.getAtracoes().add(criarAtracao(resultado));
			}
			stAtracao.close();

			PreparedStatement stAlimento = conexao.prepareStatement(SELECT_QUERY_ALIMENTO + " where PEDIDO.id = " + id);
            resultado = stAlimento.executeQuery();
            while (resultado.next()) {
                pedido.getAlimentosQuantidade().add(criarAlimentoQuantidade(resultado));
            }
            stAlimento.close();
			return pedido;
		} catch (SQLException u) {
			throw new RuntimeException(u);
		}
	}

	private Pedido criarPedido(ResultSet resultado) throws SQLException {
		Pedido pedido = new Pedido();
		pedido.setId(resultado.getInt("id"));
        pedido.setData_pedido(resultado.getDate("data_pedido").toLocalDate());
        pedido.setTotal(resultado.getFloat("total"));
        pedido.setLocal_id(resultado.getInt("local_id"));
        pedido.setUsuario_id(resultado.getInt("usuario_id"));
        return pedido;
	}
	
	private Atracao criarAtracao(ResultSet resultado) throws SQLException {
		Atracao atracao = new Atracao();
        atracao.setId(resultado.getInt("id"));
        atracao.setNome(resultado.getString("nome"));
        atracao.setValor(resultado.getFloat("valor"));
        atracao.setUrlImg(resultado.getString("atracao_imagem"));
        return atracao;
	}

	private AlimentoQuantidade criarAlimentoQuantidade(ResultSet resultado) throws SQLException {
        Alimento alimento = new Alimento();
        alimento.setId(resultado.getInt("id"));
        alimento.setNome(resultado.getString("nome"));
        alimento.setValor(resultado.getFloat("valor"));
        alimento.setQuantidade(resultado.getInt("quantidade"));
        alimento.setUrlImg(resultado.getString("imagem_alimento"));
        int quantidade = resultado.getInt("pedido_alimento_quantidade");
        return new AlimentoQuantidade(alimento, quantidade);
    }

    private Pedido getPedidoById(List<Pedido> pedidos, int id) {
		return pedidos.stream().filter(p -> p.getId() == id).findFirst().get();
    }
}
