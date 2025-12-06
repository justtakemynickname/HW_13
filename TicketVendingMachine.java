package hw_13;

public class TicketVendingMachine {
    private final State idleState = new IdleState();
    private final State waitingForMoneyState = new WaitingForMoneyState();
    private final State moneyReceivedState = new MoneyReceivedState();
    private final State ticketDispensedState = new TicketDispensedState();
    private final State transactionCanceledState = new TransactionCanceledState();

    private State currentState = idleState;
    private String selectedTicket;
    private int price;
    private int balance;
    private int ticketsAvailable = 10;

    public interface State {
        void selectTicket(String ticketType, int price);
        void insertMoney(int amount);
        void cancel();
        void dispense();
    }

    private class IdleState implements State {
        public void selectTicket(String ticketType, int price) {
            selectedTicket = ticketType;
            TicketVendingMachine.this.price = price;
            balance = 0;
            currentState = waitingForMoneyState;
            System.out.println("Выбран билет: " + ticketType + ", цена: " + price);
        }
        public void insertMoney(int amount) {
            System.out.println("Сначала выберите билет");
        }
        public void cancel() {
            System.out.println("Нет активной транзакции");
        }
        public void dispense() {
            System.out.println("Нечего выдавать");
        }
    }

    private class WaitingForMoneyState implements State {
        public void selectTicket(String ticketType, int price) {
            System.out.println("Транзакция в процессе: отмените или дождитесь завершения");
        }
        public void insertMoney(int amount) {
            balance += amount;
            System.out.println("Внесено: " + amount + ", текущий баланс: " + balance + ", требуется: " + price);
            if (balance >= price) {
                currentState = moneyReceivedState;
                System.out.println("Достаточно средств");
            }
        }
        public void cancel() {
            currentState = transactionCanceledState;
            currentState.cancel();
        }
        public void dispense() {
            System.out.println("Недостаточно средств");
        }
    }

    private class MoneyReceivedState implements State {
        public void selectTicket(String ticketType, int price) {
            System.out.println("Транзакция уже содержит выбранный билет");
        }
        public void insertMoney(int amount) {
            balance += amount;
            System.out.println("Внесено: " + amount + ", текущий баланс: " + balance);
        }
        public void cancel() {
            currentState = transactionCanceledState;
            currentState.cancel();
        }
        public void dispense() {
            currentState = ticketDispensedState;
            currentState.dispense();
        }
    }

    private class TicketDispensedState implements State {
        public void selectTicket(String ticketType, int price) {
            System.out.println("Подождите, идет выдача");
        }
        public void insertMoney(int amount) {
            System.out.println("Выдача в процессе, отказано");
        }
        public void cancel() {
            System.out.println("Слишком поздно для отмены");
        }
        public void dispense() {
            if (ticketsAvailable > 0) {
                ticketsAvailable--;
                int change = balance - price;
                System.out.println("Билет '" + selectedTicket + "' выдан");
                if (change > 0) System.out.println("Сдача: " + change);
                selectedTicket = null;
                price = 0;
                balance = 0;
                currentState = idleState;
            } else {
                System.out.println("Билетов нет, возврат средств");
                int refund = balance;
                balance = 0;
                selectedTicket = null;
                price = 0;
                System.out.println("Возврат: " + refund);
                currentState = idleState;
            }
        }
    }

    private class TransactionCanceledState implements State {
        public void selectTicket(String ticketType, int price) {
            System.out.println("Сначала завершите отмену");
        }
        public void insertMoney(int amount) {
            System.out.println("Транзакция отменена, вернуть деньги");
        }
        public void cancel() {
            if (balance > 0) System.out.println("Возврат: " + balance);
            selectedTicket = null;
            price = 0;
            balance = 0;
            currentState = idleState;
        }
        public void dispense() {
            System.out.println("Транзакция отменена, выдача невозможна");
        }
    }

    public void selectTicket(String ticketType, int price) {
        currentState.selectTicket(ticketType, price);
    }

    public void insertMoney(int amount) {
        currentState.insertMoney(amount);
    }

    public void cancel() {
        currentState.cancel();
    }

    public void dispense() {
        currentState.dispense();
    }

    public int getTicketsAvailable() {
        return ticketsAvailable;
    }

    public static void main(String[] args) {
        TicketVendingMachine m = new TicketVendingMachine();
        m.selectTicket("Single", 50);
        m.insertMoney(20);
        m.insertMoney(30);
        m.dispense();
        m.selectTicket("DayPass", 120);
        m.insertMoney(100);
        m.cancel();
        m.selectTicket("Single", 50);
        m.insertMoney(60);
        m.dispense();
    }
}
