package org.maples.serinus.algorithm;

import java.util.Scanner;

public class ListNodeTest {
    static class ListNode {
        int value;
        ListNode next;
    }

    // Input List with head node;
    private static ListNode input() {
        ListNode root = new ListNode();

        ListNode temp = root;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            int num = scanner.nextInt();
            temp.next = new ListNode();
            temp = temp.next;
            temp.value = num;
        }

        return root;
    }

    private static void output(ListNode root) {
        ListNode temp = root.next;
        while (temp != null) {
            System.out.print(temp.value);
            if (temp.next != null) {
                System.out.print("->");
            }

            temp = temp.next;
        }
    }

    public static void main(String[] args) {
        ListNode root = input();

        output(root);
    }
}
