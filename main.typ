= Trabalho

- Dono de uma empresa de gerenciamento de condomínio;

= Problema

Reserva de área comum, e.g. área de lazer, salão de festas, churrasqueira.

Na maioria dos condomínios tem uma zeladora que consegue apoiar, mas ela não
trabalha de sábado e domingo, e trabalha somente até às 18h.

Só não pode fazer uso da área às segundas-feiras.

Hoje, o controle é em papel.

- Já aconteceu de moradores apagarem o nome de outro e colocar o próprio
- Uso da área comum de terça a quinta é permitido uso somente até 23h30, aos
  finais de semana até 1h da manhã.

É necessário automatizar isto, de maneira que os condôminos não possam mais
alterar reservas alheias. O uso do sistema vai ser somente da zeladora, então
não é preciso ter autenticação, nem deploy na nuvem. A única necessidade é
remover o papel e caneta da equação.

= Requisitos

+ O zelador deve ser capaz de agendar reservas das áreas comuns.
+ A reserva deve conter o nome, número do apartamento.
+ O sistema deve permitir que o zelador cadastre as áreas comuns (e.g. área de
  churrasco, lazer, salão de festas).
+ O sistema deve permitir que o zelador altere as regras de horário por área
  comum.

= Fora do escopo

+ Autenticação.
+ Interface em tempo real para monitoramento de todos os condomínios.

= Observações

+ ~Local-first~ Local-only

= Novos requisitos

Cadastro de convidados. Necessário RG, CPF e nome. O convidado é atrelado à
reserva.

A área comum deve ter um novo campo para especificar o máximo de convidados. O
número de convidados de uma reserva deve ser limitado a este novo campo.

O morador é incluído nesta contagem.
