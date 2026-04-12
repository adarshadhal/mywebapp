# Instance Creation
resource "aws_instance" "my-ec2" {
    ami = "ami-05d2d839d4f73aafb"
    key_name = "Adarsha2580"
    instance_type = "t3.micro"
    vpc_security_group_ids = [aws_security_group.security-group.id]
    subnet_id = aws_subnet.public-subnet.id
    tags = {
        Name = "My-ec2-1"
        }
}
